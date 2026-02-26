package org.clickenrent.authservice.service;

import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jose.proc.SecurityContext;
import io.github.resilience4j.retry.Retry;
import org.clickenrent.authservice.config.AppleOAuthConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.clickenrent.authservice.dto.AppleTokenResponse;
import org.clickenrent.authservice.dto.AuthResponse;
import org.clickenrent.authservice.entity.GlobalRole;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.exception.UnauthorizedException;
import org.clickenrent.authservice.mapper.UserMapper;
import org.clickenrent.authservice.metrics.OAuthMetrics;
import org.clickenrent.authservice.repository.GlobalRoleRepository;
import org.clickenrent.authservice.repository.UserCompanyRepository;
import org.clickenrent.authservice.repository.UserGlobalRoleRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AppleOAuthService.
 * Tests OAuth flow, user creation, auto-linking, and error handling.
 */
@ExtendWith(MockitoExtension.class)
class AppleOAuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private GlobalRoleRepository globalRoleRepository;
    
    @Mock
    private UserGlobalRoleRepository userGlobalRoleRepository;
    
    @Mock
    private UserCompanyRepository userCompanyRepository;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private CustomUserDetailsService userDetailsService;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private AppleOAuthConfig appleOAuthConfig;
    
    @Mock
    private OAuthMetrics oAuthMetrics;
    
    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private Retry tokenExchangeRetry;
    
    @Mock
    private JWSSigner appleJwtSigner;
    
    @Mock
    private ConfigurableJWTProcessor<SecurityContext> appleJwtProcessor;
    
    private AppleOAuthService appleOAuthService;
    
    private static final String TEST_CLIENT_ID = "com.clickenrent.service";
    private static final String TEST_TOKEN_URI = "https://appleid.apple.com/auth/token";
    private static final String TEST_CODE = "test-auth-code";
    private static final String TEST_REDIRECT_URI = "http://localhost:3000/callback";
    private static final String TEST_ACCESS_TOKEN = "test-access-token";
    private static final String TEST_ID_TOKEN = "test-id-token";
    private static final String TEST_CLIENT_SECRET = "test-client-secret-jwt";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PRIVATE_EMAIL = "abc123@privaterelay.appleid.com";
    private static final String TEST_APPLE_SUB = "apple-user-123";
    
    @BeforeEach
    void setUp() {
        // Create ObjectProvider mocks
        @SuppressWarnings("unchecked")
        ObjectProvider<JWSSigner> signerProvider = mock(ObjectProvider.class);
        when(signerProvider.getIfAvailable()).thenReturn(appleJwtSigner);
        
        @SuppressWarnings("unchecked")
        ObjectProvider<ConfigurableJWTProcessor<SecurityContext>> processorProvider = mock(ObjectProvider.class);
        when(processorProvider.getIfAvailable()).thenReturn(appleJwtProcessor);
        
        appleOAuthService = new AppleOAuthService(
                userRepository,
                globalRoleRepository,
                userGlobalRoleRepository,
                userCompanyRepository,
                jwtService,
                userDetailsService,
                userMapper,
                appleOAuthConfig,
                oAuthMetrics,
                restTemplate,
                tokenExchangeRetry,
                signerProvider,
                processorProvider
        );
        
        // Set private fields using reflection
        ReflectionTestUtils.setField(appleOAuthService, "clientId", TEST_CLIENT_ID);
        ReflectionTestUtils.setField(appleOAuthService, "tokenUri", TEST_TOKEN_URI);
        ReflectionTestUtils.setField(appleOAuthService, "verifyIdToken", true);
        
        // Setup metrics mock to return timer sample
        when(oAuthMetrics.startFlowTimer(anyString())).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));
        
        // Setup retry to execute supplier immediately (service uses Supplier, not CheckedSupplier)
        when(tokenExchangeRetry.executeSupplier(any(Supplier.class))).thenAnswer(invocation ->
                invocation.getArgument(0, Supplier.class).get());
    }
    
    @Test
    void testAuthenticateWithApple_NewUser_Success() throws Exception {
        // Given: Apple returns valid tokens and user info
        AppleTokenResponse tokenResponse = AppleTokenResponse.builder()
                .accessToken(TEST_ACCESS_TOKEN)
                .idToken(TEST_ID_TOKEN)
                .expiresIn(3600)
                .build();
        
        when(appleOAuthConfig.generateClientSecret(any())).thenReturn(TEST_CLIENT_SECRET);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(AppleTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(tokenResponse, HttpStatus.OK));
        
        // Mock ID token verification
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(TEST_APPLE_SUB)
                .claim("email", TEST_EMAIL)
                .claim("email_verified", true)
                .issuer("https://appleid.apple.com")
                .audience(TEST_CLIENT_ID)
                .expirationTime(new Date(System.currentTimeMillis() + 3600000))
                .build();
        
        when(appleOAuthConfig.verifyIdToken(anyString(), any())).thenReturn(claimsSet);
        
        // No existing user
        when(userRepository.findByProviderIdAndProviderUserId("apple", TEST_APPLE_SUB)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        
        // Create new user
        User newUser = createTestUser();
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        
        // Setup role
        GlobalRole customerRole = new GlobalRole();
        customerRole.setName("CUSTOMER");
        when(globalRoleRepository.findByNameIgnoreCase("CUSTOMER")).thenReturn(Optional.of(customerRole));
        
        // Setup JWT generation
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(newUser.getUserName())
                .password("")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                .build();
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(anyMap(), any(UserDetails.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refresh-token");
        when(jwtService.getExpirationTime()).thenReturn(86400000L);
        when(userCompanyRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());
        
        // When: Authenticating with Apple
        AuthResponse response = appleOAuthService.authenticateWithApple(TEST_CODE, TEST_REDIRECT_URI);
        
        // Then: New user is created and JWT tokens are returned
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        
        verify(userRepository).save(any(User.class));
        verify(userGlobalRoleRepository).save(any());
        verify(oAuthMetrics).recordLoginAttempt("apple");
        verify(oAuthMetrics).recordLoginSuccess("apple");
        verify(oAuthMetrics).recordNewUserRegistration("apple");
    }
    
    @Test
    void testAuthenticateWithApple_ExistingUserByProvider_Success() throws Exception {
        // Given: User already exists with Apple provider
        User existingUser = createTestUser();
        existingUser.setProviderId("apple");
        existingUser.setProviderUserId(TEST_APPLE_SUB);
        
        when(appleOAuthConfig.generateClientSecret(any())).thenReturn(TEST_CLIENT_SECRET);
        
        AppleTokenResponse tokenResponse = AppleTokenResponse.builder()
                .accessToken(TEST_ACCESS_TOKEN)
                .idToken(TEST_ID_TOKEN)
                .build();
        
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(AppleTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(tokenResponse, HttpStatus.OK));
        
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(TEST_APPLE_SUB)
                .claim("email", TEST_EMAIL)
                .claim("email_verified", true)
                .issuer("https://appleid.apple.com")
                .audience(TEST_CLIENT_ID)
                .expirationTime(new Date(System.currentTimeMillis() + 3600000))
                .build();
        
        when(appleOAuthConfig.verifyIdToken(anyString(), any())).thenReturn(claimsSet);
        when(userRepository.findByProviderIdAndProviderUserId("apple", TEST_APPLE_SUB))
                .thenReturn(Optional.of(existingUser));
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(existingUser.getUserName())
                .password("")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                .build();
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(anyMap(), any(UserDetails.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refresh-token");
        when(jwtService.getExpirationTime()).thenReturn(86400000L);
        when(userCompanyRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());
        
        // When
        AuthResponse response = appleOAuthService.authenticateWithApple(TEST_CODE, TEST_REDIRECT_URI);
        
        // Then
        assertNotNull(response);
        verify(userRepository, never()).save(any(User.class));
        verify(oAuthMetrics).recordLoginSuccess("apple");
        verify(oAuthMetrics, never()).recordNewUserRegistration("apple");
    }
    
    @Test
    void testAuthenticateWithApple_AutoLinkToVerifiedEmail_Success() throws Exception {
        // Given: User exists with verified email
        User existingUser = createTestUser();
        existingUser.setIsEmailVerified(true);
        existingUser.setProviderId(null);
        existingUser.setProviderUserId(null);
        
        when(appleOAuthConfig.generateClientSecret(any())).thenReturn(TEST_CLIENT_SECRET);
        
        AppleTokenResponse tokenResponse = AppleTokenResponse.builder()
                .accessToken(TEST_ACCESS_TOKEN)
                .idToken(TEST_ID_TOKEN)
                .build();
        
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(AppleTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(tokenResponse, HttpStatus.OK));
        
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(TEST_APPLE_SUB)
                .claim("email", TEST_EMAIL)
                .claim("email_verified", true)
                .issuer("https://appleid.apple.com")
                .audience(TEST_CLIENT_ID)
                .expirationTime(new Date(System.currentTimeMillis() + 3600000))
                .build();
        
        when(appleOAuthConfig.verifyIdToken(anyString(), any())).thenReturn(claimsSet);
        when(userRepository.findByProviderIdAndProviderUserId("apple", TEST_APPLE_SUB))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(existingUser.getUserName())
                .password("")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                .build();
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(anyMap(), any(UserDetails.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refresh-token");
        when(jwtService.getExpirationTime()).thenReturn(86400000L);
        when(userCompanyRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());
        
        // When
        AuthResponse response = appleOAuthService.authenticateWithApple(TEST_CODE, TEST_REDIRECT_URI);
        
        // Then
        assertNotNull(response);
        verify(userRepository).save(argThat(user -> 
                "apple".equals(user.getProviderId()) && TEST_APPLE_SUB.equals(user.getProviderUserId())
        ));
        verify(oAuthMetrics).recordAutoLinking("apple");
        verify(oAuthMetrics).recordLoginSuccess("apple");
    }
    
    @Test
    void testAuthenticateWithApple_AutoLinkToUnverifiedEmail_ThrowsException() throws Exception {
        // Given: User exists with unverified email
        User existingUser = createTestUser();
        existingUser.setIsEmailVerified(false);
        
        when(appleOAuthConfig.generateClientSecret(any())).thenReturn(TEST_CLIENT_SECRET);
        
        AppleTokenResponse tokenResponse = AppleTokenResponse.builder()
                .accessToken(TEST_ACCESS_TOKEN)
                .idToken(TEST_ID_TOKEN)
                .build();
        
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(AppleTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(tokenResponse, HttpStatus.OK));
        
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(TEST_APPLE_SUB)
                .claim("email", TEST_EMAIL)
                .claim("email_verified", true)
                .issuer("https://appleid.apple.com")
                .audience(TEST_CLIENT_ID)
                .expirationTime(new Date(System.currentTimeMillis() + 3600000))
                .build();
        
        when(appleOAuthConfig.verifyIdToken(anyString(), any())).thenReturn(claimsSet);
        when(userRepository.findByProviderIdAndProviderUserId("apple", TEST_APPLE_SUB))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        
        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                appleOAuthService.authenticateWithApple(TEST_CODE, TEST_REDIRECT_URI)
        );
        
        assertTrue(exception.getMessage().contains("not verified"));
        verify(userRepository, never()).save(any(User.class));
        verify(oAuthMetrics).recordLoginFailure(eq("apple"), anyString());
    }
    
    @Test
    void testAuthenticateWithApple_PrivateRelayEmail_Success() throws Exception {
        // Given: User uses Apple private relay email
        AppleTokenResponse tokenResponse = AppleTokenResponse.builder()
                .accessToken(TEST_ACCESS_TOKEN)
                .idToken(TEST_ID_TOKEN)
                .build();
        
        when(appleOAuthConfig.generateClientSecret(any())).thenReturn(TEST_CLIENT_SECRET);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(AppleTokenResponse.class)))
                .thenReturn(new ResponseEntity<>(tokenResponse, HttpStatus.OK));
        
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(TEST_APPLE_SUB)
                .claim("email", TEST_PRIVATE_EMAIL)
                .claim("email_verified", true)
                .claim("is_private_email", true)
                .issuer("https://appleid.apple.com")
                .audience(TEST_CLIENT_ID)
                .expirationTime(new Date(System.currentTimeMillis() + 3600000))
                .build();
        
        when(appleOAuthConfig.verifyIdToken(anyString(), any())).thenReturn(claimsSet);
        when(userRepository.findByProviderIdAndProviderUserId("apple", TEST_APPLE_SUB))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_PRIVATE_EMAIL)).thenReturn(Optional.empty());
        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        
        User newUser = createTestUser();
        newUser.setEmail(TEST_PRIVATE_EMAIL);
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        
        GlobalRole customerRole = new GlobalRole();
        customerRole.setName("CUSTOMER");
        when(globalRoleRepository.findByNameIgnoreCase("CUSTOMER")).thenReturn(Optional.of(customerRole));
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(newUser.getUserName())
                .password("")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                .build();
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(anyMap(), any(UserDetails.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refresh-token");
        when(jwtService.getExpirationTime()).thenReturn(86400000L);
        when(userCompanyRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());
        
        // When
        AuthResponse response = appleOAuthService.authenticateWithApple(TEST_CODE, TEST_REDIRECT_URI);
        
        // Then
        assertNotNull(response);
        verify(userRepository).save(argThat(user -> TEST_PRIVATE_EMAIL.equals(user.getEmail())));
        verify(oAuthMetrics).recordNewUserRegistration("apple");
    }
    
    @Test
    void testAuthenticateWithApple_InvalidCode_ThrowsException() {
        // Given: Apple returns error for invalid code
        when(appleOAuthConfig.generateClientSecret(any())).thenReturn(TEST_CLIENT_SECRET);
        lenient().when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(AppleTokenResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "invalid_grant"));

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                appleOAuthService.authenticateWithApple(TEST_CODE, TEST_REDIRECT_URI)
        );
        
        assertTrue(exception.getMessage().contains("Failed to authenticate with Apple"));
        verify(oAuthMetrics).recordLoginAttempt("apple");
        verify(oAuthMetrics).recordLoginFailure(eq("apple"), anyString());
    }
    
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUserName("testuser");
        user.setEmail(TEST_EMAIL);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setIsActive(true);
        user.setIsEmailVerified(true);
        return user;
    }
}
