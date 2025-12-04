package org.clickenrent.authservice.service;

import org.clickenrent.authservice.dto.UserDTO;
import org.clickenrent.authservice.entity.Language;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.UserMapper;
import org.clickenrent.authservice.repository.LanguageRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;
    private Language language;

    @BeforeEach
    void setUp() {
        language = Language.builder()
                .id(1L)
                .name("English")
                .build();

        user = User.builder()
                .id(1L)
                .externalId("ext-123")
                .userName("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .phone("+1234567890")
                .language(language)
                .isActive(true)
                .isDeleted(false)
                .build();

        userDTO = UserDTO.builder()
                .id(1L)
                .externalId("ext-123")
                .userName("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .phone("+1234567890")
                .languageId(1L)
                .isActive(true)
                .isDeleted(false)
                .build();
    }

    @Test
    void getAllUsers_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Arrays.asList(user));
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(any(User.class))).thenReturn(userDTO);

        // When
        Page<UserDTO> result = userService.getAllUsers(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void getUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDTO);

        // When
        UserDTO result = userService.getUserById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserName()).isEqualTo("testuser");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");
    }

    @Test
    void getUserByExternalId_Success() {
        // Given
        when(userRepository.findByExternalId("ext-123")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDTO);

        // When
        UserDTO result = userService.getUserByExternalId("ext-123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getExternalId()).isEqualTo("ext-123");
        verify(userRepository, times(1)).findByExternalId("ext-123");
    }

    @Test
    void getUserByExternalId_NotFound_ThrowsException() {
        // Given
        when(userRepository.findByExternalId("ext-999")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserByExternalId("ext-999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("externalId");
    }

    @Test
    void createUser_Success() {
        // Given
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(languageRepository.findById(1L)).thenReturn(Optional.of(language));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);

        // When
        UserDTO result = userService.createUser(userDTO, "password123");

        // Then
        assertThat(result).isNotNull();
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_WithInvalidLanguage_ThrowsException() {
        // Given
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(languageRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.createUser(userDTO, "password123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language");
    }

    @Test
    void createUser_WithoutLanguage_Success() {
        // Given
        userDTO.setLanguageId(null);
        user.setLanguage(null);
        
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);

        // When
        UserDTO result = userService.createUser(userDTO, "password123");

        // Then
        assertThat(result).isNotNull();
        verify(languageRepository, never()).findById(anyLong());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(languageRepository.findById(1L)).thenReturn(Optional.of(language));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);

        // When
        UserDTO result = userService.updateUser(1L, userDTO);

        // Then
        assertThat(result).isNotNull();
        verify(userMapper, times(1)).updateEntityFromDto(userDTO, user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_NotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(1L, userDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");
    }

    @Test
    void deleteUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).save(user);
        // Verify that the user is marked as deleted and inactive
        assertThat(user.getIsDeleted()).isTrue();
        assertThat(user.getIsActive()).isFalse();
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");
    }

    @Test
    void activateUser_Success() {
        // Given
        user.setIsActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);

        // When
        UserDTO result = userService.activateUser(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(user.getIsActive()).isTrue();
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deactivateUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);

        // When
        UserDTO result = userService.deactivateUser(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(user.getIsActive()).isFalse();
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void activateUser_NotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.activateUser(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");
    }

    @Test
    void deactivateUser_NotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deactivateUser(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");
    }
}


