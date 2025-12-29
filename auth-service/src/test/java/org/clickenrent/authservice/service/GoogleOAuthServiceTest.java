package org.clickenrent.authservice.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.clickenrent.authservice.dto.AuthResponse;
import org.clickenrent.authservice.dto.GoogleTokenResponse;
import org.clickenrent.authservice.dto.GoogleUserInfo;
import org.clickenrent.authservice.entity.GlobalRole;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserGlobalRole;
import org.clickenrent.authservice.exception.UnauthorizedException;
import org.clickenrent.authservice.mapper.UserMapper;
import org.clickenrent.authservice.metrics.OAuthMetrics;
import org.clickenrent.authservice.repository.GlobalRoleRepository;
import org.clickenrent.authservice.repository.UserGlobalRoleRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GoogleOAuthService.
 * Tests OAuth flow, user creation, auto-linking, and error handling.
 */
@ExtendWith(MockitoExtension.class)
class GoogleOAuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private GlobalRoleRepository globalRoleRepository;
    
    @Mock
    private UserGlobalRoleRepository userGlobalRoleRepository;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private CustomUserDetailsService userDetailsService;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private GoogleIdTokenVerifier googleIdTokenVerifier;
    
    @Mock
    private OAuthMetrics oAuthMetrics;
    
    @Mock
    private RestTemplate restTemplate;
    
    @InjectMocks
    private GoogleOAuthService googleOAuthService;
    
    private static final String TEST_CLIENT_ID = "test-client-id";
    private static final String TEST_CLIENT_SECRET = "test-client-secret";
    private static final String TEST_TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String TEST_USER_INFO_URI = "https://www.googleapis.com/oauth2/v2/userinfo";
    private static final String TEST_CODE = "test-auth-code";
    private static final String TEST_REDIRECT_URI = "http://localhost:3000/callback";
    private static final String TEST_ACCESS_TOKEN = "test-access-token";
    private static final String TEST_ID_TOKEN = "test-id-token";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_GOOGLE_ID = "google-user-123";
    
    @BeforeEach
    void setUp() {
        // Set private fields using reflection
        ReflectionTestUtils.setField(googleOAuthService, "clientId", TEST_CLIENT_ID);
        ReflectionTestUtils.setField(googleOAuthService, "clientSecret", TEST_CLIENT_SECRET);
        ReflectionTestUtils.setField(googleOAuthService, "tokenUri", TEST_TOKEN_URI);
        ReflectionTestUtils.setField(googleOAuthService, "userInfoUri", TEST_USER_INFO_URI);
        ReflectionTestUtils.setField(googleOAuthService, "verifyIdToken", true);
        ReflectionTestUtils.setField(googleOAuthService, "restTemplate", restTemplate);
        
        // Setup metrics mock to return timer sample
        when(oAuthMetrics.startFlowTimer(anyString())).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));
    }
    
    @Test
    void authenticateWithGoogle_NewUser_Success() throws Exception {
        // Arrange
        GoogleTokenResponse tokenResponse = createTokenResponse();
        GoogleUserInfo userInfo = createGoogleUserInfo();
        User newUser = createUser();
        GlobalRole customerRole = createCustomerRole();
        UserDetails userDetails = createUserDetails();
        
        when(restTemplate.exchange(eq(TEST_TOKEN_URI), eq(HttpMethod.POST), any(HttpEntity.class), eq(GoogleTokenResponse.class)))
                .thenReturn(ResponseEntity.ok(tokenResponse));
        
        GoogleIdToken mockIdToken = mock(GoogleIdToken.class);
        GoogleIdToken.Payload mockPayload = mock(GoogleIdToken.Payload.class);
        when(googleIdTokenVerifier.verify(TEST_ID_TOKEN)).thenReturn(mockIdToken);
        when(mockIdToken.getPayload()).thenReturn(mockPayload);
        when(mockPayload.getAudience()).thenReturn(TEST_CLIENT_ID);
        when(mockPayload.getIssuer()).thenReturn("accounts.google.com");
        when(mockPayload.getEmail()).thenReturn(TEST_EMAIL);
        
        when(restTemplate.exchange(eq(TEST_USER_INFO_URI), eq(HttpMethod.GET), any(HttpEntity.class), eq(GoogleUserInfo.class)))
                .thenReturn(ResponseEntity.ok(userInfo));
        
        when(userRepository.findByProviderIdAndProviderUserId("google", TEST_GOOGLE_ID))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        
        when(globalRoleRepository.findByNameIgnoreCase("CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(userGlobalRoleRepository.save(any(UserGlobalRole.class))).thenReturn(new UserGlobalRole());
        
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(anyMap(), any(UserDetails.class))).thenReturn("jwt-access-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("jwt-refresh-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);
        when(userMapper.toDto(any(User.class))).thenReturn(null);
        
        // Act
        AuthResponse response = googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI);
        
        // Assert
        assertNotNull(response);
        assertEquals("jwt-access-token", response.getAccessToken());
        assertEquals("jwt-refresh-token", response.getRefreshToken());
        
        verify(oAuthMetrics).recordLoginAttempt("google");
        verify(oAuthMetrics).recordLoginSuccess("google");
        verify(oAuthMetrics).recordNewUserRegistration("google");
        verify(userRepository).save(any(User.class));
        verify(userGlobalRoleRepository).save(any(UserGlobalRole.class));
    }
    
    @Test
    void authenticateWithGoogle_ExistingUserByProvider_Success() throws Exception {
        // Arrange
        GoogleTokenResponse tokenResponse = createTokenResponse();
        GoogleUserInfo userInfo = createGoogleUserInfo();
        User existingUser = createUser();
        UserDetails userDetails = createUserDetails();
        
        when(restTemplate.exchange(eq(TEST_TOKEN_URI), eq(HttpMethod.POST), any(HttpEntity.class), eq(GoogleTokenResponse.class)))
                .thenReturn(ResponseEntity.ok(tokenResponse));
        
        GoogleIdToken mockIdToken = mock(GoogleIdToken.class);
        GoogleIdToken.Payload mockPayload = mock(GoogleIdToken.Payload.class);
        when(googleIdTokenVerifier.verify(TEST_ID_TOKEN)).thenReturn(mockIdToken);
        when(mockIdToken.getPayload()).thenReturn(mockPayload);
        when(mockPayload.getAudience()).thenReturn(TEST_CLIENT_ID);
        when(mockPayload.getIssuer()).thenReturn("accounts.google.com");
        when(mockPayload.getEmail()).thenReturn(TEST_EMAIL);
        
        when(restTemplate.exchange(eq(TEST_USER_INFO_URI), eq(HttpMethod.GET), any(HttpEntity.class), eq(GoogleUserInfo.class)))
                .thenReturn(ResponseEntity.ok(userInfo));
        
        when(userRepository.findByProviderIdAndProviderUserId("google", TEST_GOOGLE_ID))
                .thenReturn(Optional.of(existingUser));
        
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(anyMap(), any(UserDetails.class))).thenReturn("jwt-access-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("jwt-refresh-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);
        when(userMapper.toDto(any(User.class))).thenReturn(null);
        
        // Act
        AuthResponse response = googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI);
        
        // Assert
        assertNotNull(response);
        verify(oAuthMetrics).recordLoginSuccess("google");
        verify(userRepository, never()).save(any(User.class)); // No new user created
    }
    
    @Test
    void authenticateWithGoogle_AutoLinkingVerifiedEmail_Success() throws Exception {
        // Arrange
        GoogleTokenResponse tokenResponse = createTokenResponse();
        GoogleUserInfo userInfo = createGoogleUserInfo();
        User existingUser = createUser();
        existingUser.setIsEmailVerified(true); // Email is verified
        existingUser.setProviderId(null); // Not linked to any provider yet
        UserDetails userDetails = createUserDetails();
        
        when(restTemplate.exchange(eq(TEST_TOKEN_URI), eq(HttpMethod.POST), any(HttpEntity.class), eq(GoogleTokenResponse.class)))
                .thenReturn(ResponseEntity.ok(tokenResponse));
        
        GoogleIdToken mockIdToken = mock(GoogleIdToken.class);
        GoogleIdToken.Payload mockPayload = mock(GoogleIdToken.Payload.class);
        when(googleIdTokenVerifier.verify(TEST_ID_TOKEN)).thenReturn(mockIdToken);
        when(mockIdToken.getPayload()).thenReturn(mockPayload);
        when(mockPayload.getAudience()).thenReturn(TEST_CLIENT_ID);
        when(mockPayload.getIssuer()).thenReturn("accounts.google.com");
        when(mockPayload.getEmail()).thenReturn(TEST_EMAIL);
        
        when(restTemplate.exchange(eq(TEST_USER_INFO_URI), eq(HttpMethod.GET), any(HttpEntity.class), eq(GoogleUserInfo.class)))
                .thenReturn(ResponseEntity.ok(userInfo));
        
        when(userRepository.findByProviderIdAndProviderUserId("google", TEST_GOOGLE_ID))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(anyMap(), any(UserDetails.class))).thenReturn("jwt-access-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("jwt-refresh-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);
        when(userMapper.toDto(any(User.class))).thenReturn(null);
        
        // Act
        AuthResponse response = googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI);
        
        // Assert
        assertNotNull(response);
        verify(oAuthMetrics).recordAutoLinking("google");
        verify(userRepository).save(argThat(user -> 
            "google".equals(user.getProviderId()) && TEST_GOOGLE_ID.equals(user.getProviderUserId())
        ));
    }
    
    @Test
    void authenticateWithGoogle_AutoLinkingUnverifiedEmail_ThrowsException() throws Exception {
        // Arrange
        GoogleTokenResponse tokenResponse = createTokenResponse();
        GoogleUserInfo userInfo = createGoogleUserInfo();
        User existingUser = createUser();
        existingUser.setIsEmailVerified(false); // Email is NOT verified
        existingUser.setProviderId(null);
        
        when(restTemplate.exchange(eq(TEST_TOKEN_URI), eq(HttpMethod.POST), any(HttpEntity.class), eq(GoogleTokenResponse.class)))
                .thenReturn(ResponseEntity.ok(tokenResponse));
        
        GoogleIdToken mockIdToken = mock(GoogleIdToken.class);
        GoogleIdToken.Payload mockPayload = mock(GoogleIdToken.Payload.class);
        when(googleIdTokenVerifier.verify(TEST_ID_TOKEN)).thenReturn(mockIdToken);
        when(mockIdToken.getPayload()).thenReturn(mockPayload);
        when(mockPayload.getAudience()).thenReturn(TEST_CLIENT_ID);
        when(mockPayload.getIssuer()).thenReturn("accounts.google.com");
        when(mockPayload.getEmail()).thenReturn(TEST_EMAIL);
        
        when(restTemplate.exchange(eq(TEST_USER_INFO_URI), eq(HttpMethod.GET), any(HttpEntity.class), eq(GoogleUserInfo.class)))
                .thenReturn(ResponseEntity.ok(userInfo));
        
        when(userRepository.findByProviderIdAndProviderUserId("google", TEST_GOOGLE_ID))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        
        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI);
        });
        
        assertTrue(exception.getMessage().contains("not verified"));
        verify(oAuthMetrics).recordLoginFailure("google", "unauthorized");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void authenticateWithGoogle_InvalidAuthCode_ThrowsException() {
        // Arrange
        when(restTemplate.exchange(eq(TEST_TOKEN_URI), eq(HttpMethod.POST), any(HttpEntity.class), eq(GoogleTokenResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid authorization code"));
        
        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI);
        });
        
        assertTrue(exception.getMessage().contains("Failed to authenticate with Google"));
        verify(oAuthMetrics).recordLoginAttempt("google");
        verify(oAuthMetrics).recordLoginFailure("google", "http_error");
    }
    
    @Test
    void authenticateWithGoogle_InvalidIdToken_ThrowsException() throws Exception {
        // Arrange
        GoogleTokenResponse tokenResponse = createTokenResponse();
        
        when(restTemplate.exchange(eq(TEST_TOKEN_URI), eq(HttpMethod.POST), any(HttpEntity.class), eq(GoogleTokenResponse.class)))
                .thenReturn(ResponseEntity.ok(tokenResponse));
        
        when(googleIdTokenVerifier.verify(TEST_ID_TOKEN)).thenReturn(null); // Invalid token
        
        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI);
        });
        
        assertTrue(exception.getMessage().contains("Failed to authenticate with Google"));
        verify(oAuthMetrics).recordLoginFailure(eq("google"), anyString());
    }
    
    @Test
    void authenticateWithGoogle_GoogleApiFailure_ThrowsException() {
        // Arrange
        when(restTemplate.exchange(eq(TEST_TOKEN_URI), eq(HttpMethod.POST), any(HttpEntity.class), eq(GoogleTokenResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Google API error"));
        
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI);
        });
        
        verify(oAuthMetrics).recordLoginFailure("google", "http_error");
    }
    
    // Helper methods
    
    private GoogleTokenResponse createTokenResponse() {
        return GoogleTokenResponse.builder()
                .accessToken(TEST_ACCESS_TOKEN)
                .idToken(TEST_ID_TOKEN)
                .expiresIn(3600)
                .tokenType("Bearer")
                .build();
    }
    
    private GoogleUserInfo createGoogleUserInfo() {
        return GoogleUserInfo.builder()
                .id(TEST_GOOGLE_ID)
                .email(TEST_EMAIL)
                .verifiedEmail(true)
                .name("Test User")
                .givenName("Test")
                .familyName("User")
                .picture("https://example.com/picture.jpg")
                .build();
    }
    
    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setExternalId("ext-123");
        user.setUserName("testuser");
        user.setEmail(TEST_EMAIL);
        user.setProviderId("google");
        user.setProviderUserId(TEST_GOOGLE_ID);
        user.setIsEmailVerified(true);
        user.setIsActive(true);
        user.setIsDeleted(false);
        return user;
    }
    
    private GlobalRole createCustomerRole() {
        GlobalRole role = new GlobalRole();
        role.setId(1L);
        role.setName("CUSTOMER");
        return role;
    }
    
    private UserDetails createUserDetails() {
        return new org.springframework.security.core.userdetails.User(
                "testuser",
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
    }
}

