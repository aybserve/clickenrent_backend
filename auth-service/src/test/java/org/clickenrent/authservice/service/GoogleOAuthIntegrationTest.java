package org.clickenrent.authservice.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.clickenrent.authservice.client.SearchServiceClient;
import org.clickenrent.authservice.dto.AuthResponse;
import org.clickenrent.authservice.entity.GlobalRole;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.event.IndexEventPublisher;
import org.clickenrent.authservice.exception.UnauthorizedException;
import org.clickenrent.authservice.metrics.OAuthMetrics;
import org.clickenrent.authservice.repository.GlobalRoleRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Integration tests for Google OAuth flow.
 * Uses MockWebServer to simulate Google API responses.
 * Tests the complete flow from authorization code to JWT generation.
 * Uses H2 in-memory database for fast, isolated testing.
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class GoogleOAuthIntegrationTest {
    
    @Autowired
    private GoogleOAuthService googleOAuthService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GlobalRoleRepository globalRoleRepository;
    
    @MockBean
    private JwtService jwtService;
    
    @MockBean
    private CustomUserDetailsService userDetailsService;
    
    @MockBean
    private GoogleIdTokenVerifier googleIdTokenVerifier;
    
    @MockBean
    private OAuthMetrics oAuthMetrics;

    @MockBean
    private IndexEventPublisher indexEventPublisher;

    @MockBean
    private SearchServiceClient searchServiceClient;

    private MockWebServer mockGoogleServer;
    
    private static final String TEST_CODE = "test-authorization-code";
    private static final String TEST_REDIRECT_URI = "http://localhost:3000/callback";
    private static final String TEST_ACCESS_TOKEN = "ya29.test-access-token";
    private static final String TEST_GOOGLE_ID = "google-user-123456";
    private static final String TEST_EMAIL = "integration.test@example.com";
    
    @BeforeEach
    void setUp() throws IOException {
        // Start mock server
        mockGoogleServer = new MockWebServer();
        mockGoogleServer.start();
        
        // Configure service to use mock server
        String mockServerUrl = mockGoogleServer.url("/").toString();
        ReflectionTestUtils.setField(googleOAuthService, "tokenUri", mockServerUrl + "token");
        ReflectionTestUtils.setField(googleOAuthService, "userInfoUri", mockServerUrl + "userinfo");
        
        // Mock metrics
        when(oAuthMetrics.startFlowTimer(anyString()))
                .thenReturn(io.micrometer.core.instrument.Timer.start());
        
        // Ensure CUSTOMER role exists
        if (!globalRoleRepository.findByNameIgnoreCase("CUSTOMER").isPresent()) {
            GlobalRole customerRole = new GlobalRole();
            customerRole.setName("CUSTOMER");
            globalRoleRepository.save(customerRole);
        }
    }
    
    @AfterEach
    void tearDown() throws IOException {
        if (mockGoogleServer != null) {
            mockGoogleServer.shutdown();
        }
    }
    
    @Test
    void testCompleteOAuthFlow_NewUser_Success() throws Exception {
        // Arrange
        setupMockGoogleTokenResponse();
        setupMockGoogleUserInfoResponse();
        setupJwtMocks();
        
        // Act
        AuthResponse response = googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI);
        
        // Assert
        assertNotNull(response);
        assertEquals("mock-access-token", response.getAccessToken());
        assertEquals("mock-refresh-token", response.getRefreshToken());
        assertEquals(3600000L, response.getExpiresIn());
        
        // Verify user was created in database
        Optional<User> savedUser = userRepository.findByEmail(TEST_EMAIL);
        assertTrue(savedUser.isPresent());
        assertEquals("google", savedUser.get().getProviderId());
        assertEquals(TEST_GOOGLE_ID, savedUser.get().getProviderUserId());
        assertTrue(savedUser.get().getIsEmailVerified());
        
        // Verify HTTP requests to mock server
        assertEquals(2, mockGoogleServer.getRequestCount());
        
        RecordedRequest tokenRequest = mockGoogleServer.takeRequest();
        assertEquals("/token", tokenRequest.getPath());
        assertTrue(tokenRequest.getBody().readUtf8().contains("code=" + TEST_CODE));
        
        RecordedRequest userInfoRequest = mockGoogleServer.takeRequest();
        assertEquals("/userinfo", userInfoRequest.getPath());
        assertEquals("Bearer " + TEST_ACCESS_TOKEN, userInfoRequest.getHeader(HttpHeaders.AUTHORIZATION));
    }
    
    @Test
    void testOAuthFlow_ExistingUserByProvider_Success() throws Exception {
        // Arrange - Create existing user
        createAndSaveUser(TEST_EMAIL, TEST_GOOGLE_ID);
        
        setupMockGoogleTokenResponse();
        setupMockGoogleUserInfoResponse();
        setupJwtMocks();
        
        // Act
        AuthResponse response = googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI);
        
        // Assert
        assertNotNull(response);
        
        // Verify no duplicate user was created
        long userCount = userRepository.findAll().stream()
                .filter(u -> TEST_EMAIL.equals(u.getEmail()))
                .count();
        assertEquals(1, userCount);
    }
    
    @Test
    void testOAuthFlow_AutoLinkingVerifiedEmail_Success() throws Exception {
        // Arrange - Create user with verified email but no provider
        User existingUser = new User();
        existingUser.setExternalId("ext-" + System.currentTimeMillis());
        existingUser.setUserName("existing.user");
        existingUser.setEmail(TEST_EMAIL);
        existingUser.setIsEmailVerified(true);
        existingUser.setIsActive(true);
        existingUser.setIsDeleted(false);
        userRepository.save(existingUser);
        
        setupMockGoogleTokenResponse();
        setupMockGoogleUserInfoResponse();
        setupJwtMocks();
        
        // Act
        AuthResponse response = googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI);
        
        // Assert
        assertNotNull(response);
        
        // Verify user was linked to Google
        Optional<User> linkedUser = userRepository.findByEmail(TEST_EMAIL);
        assertTrue(linkedUser.isPresent());
        assertEquals("google", linkedUser.get().getProviderId());
        assertEquals(TEST_GOOGLE_ID, linkedUser.get().getProviderUserId());
    }
    
    @Test
    void testOAuthFlow_AutoLinkingUnverifiedEmail_ThrowsException() throws Exception {
        // Arrange - Create user with UNVERIFIED email
        User existingUser = new User();
        existingUser.setExternalId("ext-" + System.currentTimeMillis());
        existingUser.setUserName("unverified.user");
        existingUser.setEmail(TEST_EMAIL);
        existingUser.setIsEmailVerified(false); // NOT verified
        existingUser.setIsActive(true);
        existingUser.setIsDeleted(false);
        userRepository.save(existingUser);
        
        setupMockGoogleTokenResponse();
        setupMockGoogleUserInfoResponse();
        
        // Act & Assert
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI)
        );
        
        assertTrue(exception.getMessage().contains("not verified"));
        
        // Verify user was NOT linked
        Optional<User> user = userRepository.findByEmail(TEST_EMAIL);
        assertTrue(user.isPresent());
        assertNull(user.get().getProviderId());
    }
    
    @Test
    void testOAuthFlow_InvalidAuthCode_ThrowsException() throws Exception {
        // Arrange - Mock Google returning error for invalid code
        mockGoogleServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\": \"invalid_grant\", \"error_description\": \"Invalid authorization code\"}"));
        
        // Act & Assert
        assertThrows(
                UnauthorizedException.class,
                () -> googleOAuthService.authenticateWithGoogle("invalid-code", TEST_REDIRECT_URI)
        );
    }
    
    @Test
    void testOAuthFlow_GoogleServerError_RetriesAndFails() throws Exception {
        // Arrange - Mock Google returning server errors
        for (int i = 0; i < 3; i++) {
            mockGoogleServer.enqueue(new MockResponse().setResponseCode(500));
        }
        
        // Act & Assert
        assertThrows(
                UnauthorizedException.class,
                () -> googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI)
        );
        
        // Verify retry mechanism attempted multiple times
        assertEquals(3, mockGoogleServer.getRequestCount());
    }
    
    @Test
    void testOAuthFlow_NetworkTimeout_RetriesAndSucceeds() throws Exception {
        // Arrange - First two requests timeout, third succeeds
        mockGoogleServer.enqueue(new MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE));
        mockGoogleServer.enqueue(new MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE));
        setupMockGoogleTokenResponse();
        setupMockGoogleUserInfoResponse();
        setupJwtMocks();
        
        // Act
        AuthResponse response = googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI);
        
        // Assert
        assertNotNull(response);
        // Should have made 3 attempts to token endpoint + 1 to userinfo endpoint
        assertTrue(mockGoogleServer.getRequestCount() >= 3);
    }
    
    // Helper methods
    
    private void setupMockGoogleTokenResponse() {
        String tokenResponse = String.format(
                "{\"access_token\":\"%s\",\"expires_in\":3600,\"token_type\":\"Bearer\",\"scope\":\"openid email profile\"}",
                TEST_ACCESS_TOKEN
        );
        
        mockGoogleServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(tokenResponse));
    }
    
    private void setupMockGoogleUserInfoResponse() {
        String userInfoResponse = String.format(
                "{\"id\":\"%s\",\"email\":\"%s\",\"verified_email\":true," +
                        "\"name\":\"Integration Test\",\"given_name\":\"Integration\"," +
                        "\"family_name\":\"Test\",\"picture\":\"https://example.com/photo.jpg\"}",
                TEST_GOOGLE_ID,
                TEST_EMAIL
        );
        
        mockGoogleServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(userInfoResponse));
    }
    
    private void setupJwtMocks() {
        UserDetails mockUserDetails = new org.springframework.security.core.userdetails.User(
                "integration.test",
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(jwtService.generateToken(anyMap(), any(UserDetails.class))).thenReturn("mock-access-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("mock-refresh-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);
    }
    
    private User createAndSaveUser(String email, String googleId) {
        User user = new User();
        user.setExternalId("ext-" + System.currentTimeMillis());
        user.setUserName("test.user");
        user.setEmail(email);
        user.setProviderId("google");
        user.setProviderUserId(googleId);
        user.setIsEmailVerified(true);
        user.setIsActive(true);
        user.setIsDeleted(false);
        return userRepository.save(user);
    }
}
