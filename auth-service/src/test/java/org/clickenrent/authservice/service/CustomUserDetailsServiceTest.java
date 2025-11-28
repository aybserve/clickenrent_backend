package org.clickenrent.authservice.service;

import org.clickenrent.authservice.entity.*;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.repository.UserCompanyRepository;
import org.clickenrent.authservice.repository.UserGlobalRoleRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomUserDetailsService.
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserGlobalRoleRepository userGlobalRoleRepository;

    @Mock
    private UserCompanyRepository userCompanyRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;
    private GlobalRole adminRole;
    private GlobalRole b2bRole;
    private CompanyRole ownerRole;
    private Company company;
    private UserGlobalRole userGlobalRole1;
    private UserGlobalRole userGlobalRole2;
    private UserCompany userCompany;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .externalId("ext-123")
                .userName("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .isActive(true)
                .isDeleted(false)
                .build();

        adminRole = GlobalRole.builder()
                .id(1L)
                .name("Admin")
                .build();

        b2bRole = GlobalRole.builder()
                .id(2L)
                .name("B2B")
                .build();

        ownerRole = CompanyRole.builder()
                .id(1L)
                .name("Owner")
                .build();

        company = Company.builder()
                .id(1L)
                .name("Test Company")
                .build();

        userGlobalRole1 = UserGlobalRole.builder()
                .id(1L)
                .user(user)
                .globalRole(adminRole)
                .build();

        userGlobalRole2 = UserGlobalRole.builder()
                .id(2L)
                .user(user)
                .globalRole(b2bRole)
                .build();

        userCompany = UserCompany.builder()
                .id(1L)
                .user(user)
                .company(company)
                .companyRole(ownerRole)
                .build();
    }

    @Test
    void loadUserByUsername_WithUsername_Success() {
        // Given
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));
        when(userGlobalRoleRepository.findByUser(user))
                .thenReturn(Arrays.asList(userGlobalRole1, userGlobalRole2));
        when(userCompanyRepository.findByUser(user))
                .thenReturn(Collections.singletonList(userCompany));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();

        // Check authorities
        assertThat(userDetails.getAuthorities()).hasSize(3);
        assertThat(userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority))
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_B2B", "COMPANY_OWNER_1");

        verify(userRepository, times(1)).findByUserName("testuser");
        verify(userGlobalRoleRepository, times(1)).findByUser(user);
        verify(userCompanyRepository, times(1)).findByUser(user);
    }

    @Test
    void loadUserByUsername_WithEmail_Success() {
        // Given
        when(userRepository.findByUserName("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userGlobalRoleRepository.findByUser(user)).thenReturn(Collections.emptyList());
        when(userCompanyRepository.findByUser(user)).thenReturn(Collections.emptyList());

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByUserName("test@example.com");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_NoRoles_AssignsDefaultRole() {
        // Given
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));
        when(userGlobalRoleRepository.findByUser(user)).thenReturn(Collections.emptyList());
        when(userCompanyRepository.findByUser(user)).thenReturn(Collections.emptyList());

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Then
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority))
                .containsExactly("ROLE_USER");
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findByUserName("nonexistent")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void loadUserByUsername_InactiveUser_ThrowsException() {
        // Given
        user.setIsActive(false);
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("testuser"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("not active");
    }

    @Test
    void loadUserByUsername_DeletedUser_ThrowsException() {
        // Given
        user.setIsDeleted(true);
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("testuser"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("deleted");
    }

    @Test
    void loadUserEntityByUsername_WithUsername_Success() {
        // Given
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));

        // When
        User result = customUserDetailsService.loadUserEntityByUsername("testuser");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserName()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByUserName("testuser");
    }

    @Test
    void loadUserEntityByUsername_WithEmail_Success() {
        // Given
        when(userRepository.findByUserName("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        User result = customUserDetailsService.loadUserEntityByUsername("test@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserName()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void loadUserEntityByUsername_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findByUserName("nonexistent")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> customUserDetailsService.loadUserEntityByUsername("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void loadUserByUsername_WithMultipleCompanyRoles_Success() {
        // Given
        Company company2 = Company.builder().id(2L).name("Company 2").build();
        CompanyRole staffRole = CompanyRole.builder().id(2L).name("Staff").build();
        UserCompany userCompany2 = UserCompany.builder()
                .id(2L)
                .user(user)
                .company(company2)
                .companyRole(staffRole)
                .build();

        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));
        when(userGlobalRoleRepository.findByUser(user)).thenReturn(Collections.emptyList());
        when(userCompanyRepository.findByUser(user))
                .thenReturn(Arrays.asList(userCompany, userCompany2));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Then
        assertThat(userDetails.getAuthorities()).hasSize(2);
        assertThat(userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority))
                .containsExactlyInAnyOrder("COMPANY_OWNER_1", "COMPANY_STAFF_2");
    }

    @Test
    void loadUserByUsername_RoleNamesCaseInsensitive_Success() {
        // Given
        GlobalRole lowerCaseRole = GlobalRole.builder()
                .id(3L)
                .name("customer")
                .build();
        UserGlobalRole userGlobalRole3 = UserGlobalRole.builder()
                .id(3L)
                .user(user)
                .globalRole(lowerCaseRole)
                .build();

        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));
        when(userGlobalRoleRepository.findByUser(user))
                .thenReturn(Collections.singletonList(userGlobalRole3));
        when(userCompanyRepository.findByUser(user)).thenReturn(Collections.emptyList());

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Then
        assertThat(userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority))
                .contains("ROLE_CUSTOMER");
    }
}

