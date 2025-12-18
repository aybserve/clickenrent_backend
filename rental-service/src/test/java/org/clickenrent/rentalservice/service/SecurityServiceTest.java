package org.clickenrent.rentalservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SecurityService.
 */
@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @InjectMocks
    private SecurityService securityService;

    private SecurityContext securityContext;
    private Authentication authentication;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getCurrentUserId_WithValidJwt_ReturnsUserId() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 123);
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), 
              Map.of("alg", "HS256"), claims);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        Long result = securityService.getCurrentUserId();

        // Assert
        assertEquals(123L, result);
    }

    @Test
    void getCurrentUserId_WithLongType_ReturnsUserId() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 123L);
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), 
              Map.of("alg", "HS256"), claims);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        Long result = securityService.getCurrentUserId();

        // Assert
        assertEquals(123L, result);
    }

    @Test
    void getCurrentUserId_WithoutAuthentication_ReturnsNull() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        Long result = securityService.getCurrentUserId();

        // Assert
        assertNull(result);
    }

    @Test
    void getCurrentUserCompanyIds_WithValidJwt_ReturnsCompanyIds() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("companyIds", Arrays.asList(1, 2, 3));
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), 
              Map.of("alg", "HS256"), claims);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        List<Long> result = securityService.getCurrentUserCompanyIds();

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
    }

    @Test
    void getCurrentUserCompanyIds_WithoutAuthentication_ReturnsEmptyList() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        List<Long> result = securityService.getCurrentUserCompanyIds();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getCurrentUserRoles_WithAuthorities_ReturnsRoles() {
        // Arrange
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_ADMIN"),
        new SimpleGrantedAuthority("ROLE_SUPERADMIN")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        List<String> result = securityService.getCurrentUserRoles();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("ADMIN"));
        assertTrue(result.contains("SUPERADMIN"));
    }

    @Test
    void getCurrentUserRoles_WithoutAuthentication_ReturnsEmptyList() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        List<String> result = securityService.getCurrentUserRoles();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void isAdmin_WithSuperadminRole_ReturnsTrue() {
        // Arrange
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_SUPERADMIN")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean result = securityService.isAdmin();

        // Assert
        assertTrue(result);
    }

    @Test
    void isAdmin_WithAdminRole_ReturnsTrue() {
        // Arrange
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean result = securityService.isAdmin();

        // Assert
        assertTrue(result);
    }

    @Test
    void isAdmin_WithCustomerRole_ReturnsFalse() {
        // Arrange
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean result = securityService.isAdmin();

        // Assert
        assertFalse(result);
    }

    @Test
    void isB2B_WithB2BRole_ReturnsTrue() {
        // Arrange
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_B2B")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean result = securityService.isB2B();

        // Assert
        assertTrue(result);
    }

    @Test
    void isB2B_WithoutB2BRole_ReturnsFalse() {
        // Arrange
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean result = securityService.isB2B();

        // Assert
        assertFalse(result);
    }

    @Test
    void isCustomer_WithCustomerRole_ReturnsTrue() {
        // Arrange
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean result = securityService.isCustomer();

        // Assert
        assertTrue(result);
    }

    @Test
    void isCustomer_WithoutCustomerRole_ReturnsFalse() {
        // Arrange
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean result = securityService.isCustomer();

        // Assert
        assertFalse(result);
    }

    @Test
    void hasAccessToCompany_WithAdminRole_ReturnsTrue() {
        // Arrange
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean result = securityService.hasAccessToCompany(1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void hasAccessToCompany_WithMatchingCompanyId_ReturnsTrue() {
        // Arrange
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_B2B")
        );
        Map<String, Object> claims = new HashMap<>();
        claims.put("companyIds", Arrays.asList(1, 2));
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), 
              Map.of("alg", "HS256"), claims);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        boolean result = securityService.hasAccessToCompany(1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void hasAccessToCompany_WithoutMatchingCompanyId_ReturnsFalse() {
        // Arrange
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_B2B")
        );
        Map<String, Object> claims = new HashMap<>();
        claims.put("companyIds", Arrays.asList(2, 3));
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), 
              Map.of("alg", "HS256"), claims);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        boolean result = securityService.hasAccessToCompany(1L);

        // Assert
        assertFalse(result);
    }

    @Test
    void hasAccessToUser_WithAdminRole_ReturnsTrue() {
        // Arrange
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Act
        boolean result = securityService.hasAccessToUser(999L);

        // Assert
        assertTrue(result);
    }

    @Test
    void hasAccessToUser_WithMatchingUserId_ReturnsTrue() {
        // Arrange
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 123);
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), 
              Map.of("alg", "HS256"), claims);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        boolean result = securityService.hasAccessToUser(123L);

        // Assert
        assertTrue(result);
    }

    @Test
    void hasAccessToUser_WithoutMatchingUserId_ReturnsFalse() {
        // Arrange
        @SuppressWarnings("unchecked")
        Collection<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 123);
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), 
              Map.of("alg", "HS256"), claims);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        boolean result = securityService.hasAccessToUser(999L);

        // Assert
        assertFalse(result);
    }
}


