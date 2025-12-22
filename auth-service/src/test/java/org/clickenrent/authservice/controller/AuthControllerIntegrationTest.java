package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.LoginRequest;
import org.clickenrent.authservice.dto.RefreshTokenRequest;
import org.clickenrent.authservice.dto.RegisterRequest;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        userRepository.deleteAll();

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
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void register_ValidRequest_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.expiresIn").exists())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.userName").value("testuser"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));

        // Verify user was created in database
        assertThat(userRepository.findByUserName("testuser")).isPresent();
    }

    @Test
    void register_DuplicateUsername_ReturnsConflict() throws Exception {
        // Create user first
        User user = User.builder()
                .userName("testuser")
                .email("existing@example.com")
                .password(passwordEncoder.encode("password"))
                .isActive(true)
                .isDeleted(false)
                .build();
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("username")));
    }

    @Test
    void register_DuplicateEmail_ReturnsConflict() throws Exception {
        // Create user with same email
        User user = User.builder()
                .userName("existinguser")
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .isActive(true)
                .isDeleted(false)
                .build();
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("email")));
    }

    @Test
    void register_InvalidRequest_ReturnsBadRequest() throws Exception {
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .userName("ab") // Too short
                .email("invalid-email") // Invalid email format
                .password("123") // Too short
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void login_ValidCredentials_ReturnsOk() throws Exception {
        // Create user first
        User user = User.builder()
                .userName("testuser")
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Test")
                .lastName("User")
                .isActive(true)
                .isDeleted(false)
                .build();
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.user.userName").value("testuser"));
    }

    @Test
    void login_WithEmail_ReturnsOk() throws Exception {
        // Create user first
        User user = User.builder()
                .userName("testuser")
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .isActive(true)
                .isDeleted(false)
                .build();
        userRepository.save(user);

        LoginRequest emailLoginRequest = LoginRequest.builder()
                .usernameOrEmail("test@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Create user first
        User user = User.builder()
                .userName("testuser")
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .isActive(true)
                .isDeleted(false)
                .build();
        userRepository.save(user);

        LoginRequest wrongPasswordRequest = LoginRequest.builder()
                .usernameOrEmail("testuser")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Invalid username or password")));
    }

    @Test
    void login_UserNotFound_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshToken_ValidToken_ReturnsOk() throws Exception {
        // Register user and get tokens
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponse = registerResult.getResponse().getContentAsString();
        String refreshToken = objectMapper.readTree(registerResponse).get("refreshToken").asText();

        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    void refreshToken_InvalidToken_ReturnsUnauthorized() throws Exception {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("invalid.token.here");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Invalid or expired")));
    }

    @Test
    void getCurrentUser_WithValidToken_ReturnsOk() throws Exception {
        // Register user and get token
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponse = registerResult.getResponse().getContentAsString();
        String accessToken = objectMapper.readTree(registerResponse).get("accessToken").asText();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getCurrentUser_WithoutToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCurrentUser_WithInvalidToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isForbidden());
    }

    @Test
    void logout_ReturnsNoContent() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    void fullAuthenticationFlow_Success() throws Exception {
        // 1. Register
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponse = registerResult.getResponse().getContentAsString();
        String accessToken = objectMapper.readTree(registerResponse).get("accessToken").asText();
        String refreshToken = objectMapper.readTree(registerResponse).get("refreshToken").asText();

        // 2. Get current user with access token
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("testuser"));

        // 3. Refresh token
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(refreshToken);
        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String refreshResponse = refreshResult.getResponse().getContentAsString();
        String newAccessToken = objectMapper.readTree(refreshResponse).get("accessToken").asText();

        // 4. Use new access token
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + newAccessToken))
                .andExpect(status().isOk());

        // 5. Logout
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent());
    }
}






