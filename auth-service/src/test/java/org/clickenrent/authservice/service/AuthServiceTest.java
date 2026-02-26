package org.clickenrent.authservice.service;

import org.clickenrent.authservice.dto.AuthResponse;
import org.clickenrent.authservice.dto.LoginRequest;
import org.clickenrent.authservice.dto.RefreshTokenRequest;
import org.clickenrent.authservice.dto.RegisterRequest;
import org.clickenrent.authservice.entity.GlobalRole;
import org.clickenrent.authservice.entity.Language;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.exception.DuplicateResourceException;
import org.clickenrent.authservice.exception.InvalidTokenException;
import org.clickenrent.authservice.exception.UnauthorizedException;
import org.clickenrent.authservice.mapper.UserMapper;
import org.clickenrent.authservice.repository.GlobalRoleRepository;
import org.clickenrent.authservice.repository.LanguageRepository;
import org.clickenrent.authservice.repository.UserCompanyRepository;
import org.clickenrent.authservice.repository.UserGlobalRoleRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GlobalRoleRepository globalRoleRepository;

    @Mock
    private UserGlobalRoleRepository userGlobalRoleRepository;

    @Mock
    private UserCompanyRepository userCompanyRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private UserPreferenceService userPreferenceService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .userName("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phone("+1234567890")
                .build();

        loginRequest = LoginRequest.builder()
                .usernameOrEmail("testuser")
                .password("password123")
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
                .isActive(true)
                .isDeleted(false)
                .build();

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("encodedPassword")
                .roles("USER")
                .build();

        // Stub services used in register/registerAdmin flows (lenient: only used in register/registerAdmin tests)
        lenient().when(userPreferenceService.createDefaultPreferences(any(User.class))).thenReturn(null);
        lenient().doNothing().when(emailVerificationService).generateAndSendCode(any(User.class));
        // Stub userCompanyRepository used in login/buildJwtClaims
        lenient().when(userCompanyRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());
    }

    @Test
    void register_Success() {
        // Given
        GlobalRole customerRole = GlobalRole.builder().id(1L).name("CUSTOMER").build();
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(globalRoleRepository.findByNameIgnoreCase("CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(Map.class), any(UserDetails.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        // Given
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("username");
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        // Given
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");
    }

    @Test
    void register_WithLanguage_Success() {
        // Given
        GlobalRole customerRole = GlobalRole.builder().id(1L).name("CUSTOMER").build();
        Language language = new Language();
        language.setId(1L);
        language.setName("English");

        registerRequest.setLanguageId(1L);

        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(languageRepository.findById(1L)).thenReturn(Optional.of(language));
        when(globalRoleRepository.findByNameIgnoreCase("CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(Map.class), any(UserDetails.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        verify(languageRepository, times(1)).findById(1L);
    }

    @Test
    void login_Success() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserEntityByUsername(anyString())).thenReturn(user);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(Map.class), any(UserDetails.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    void refreshToken_Success() {
        // Given
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("validRefreshToken");

        when(jwtService.extractUsername("validRefreshToken")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.validateToken("validRefreshToken", userDetails)).thenReturn(true);
        when(userDetailsService.loadUserEntityByUsername("testuser")).thenReturn(user);
        when(jwtService.generateToken(any(Map.class), any(UserDetails.class))).thenReturn("newAccessToken");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // When
        AuthResponse response = authService.refreshToken(refreshRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(response.getRefreshToken()).isEqualTo("validRefreshToken");
        verify(jwtService, times(1)).validateToken("validRefreshToken", userDetails);
    }

    @Test
    void refreshToken_InvalidToken_ThrowsException() {
        // Given
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("invalidToken");

        when(jwtService.extractUsername("invalidToken")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.validateToken("invalidToken", userDetails)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(refreshRequest))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid or expired refresh token");
    }

    @Test
    void refreshToken_ExpiredToken_ThrowsException() {
        // Given
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("expiredToken");

        when(jwtService.extractUsername("expiredToken")).thenThrow(new RuntimeException("Token expired"));

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(refreshRequest))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid or expired refresh token");
    }

    @Test
    void getCurrentUser_Success() {
        // Given
        when(userDetailsService.loadUserEntityByUsername("testuser")).thenReturn(user);

        // When
        authService.getCurrentUser("testuser");

        // Then
        verify(userDetailsService, times(1)).loadUserEntityByUsername("testuser");
        verify(userMapper, times(1)).toDto(user);
    }
}


