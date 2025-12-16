package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.*;
import org.clickenrent.authservice.config.SecurityConfig;
import org.clickenrent.authservice.service.AuthService;
import org.clickenrent.authservice.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    private org.clickenrent.authservice.service.CustomUserDetailsService customUserDetailsService;

    @MockBean
    private org.clickenrent.authservice.service.JwtService jwtService;

    @MockBean
    private org.clickenrent.authservice.service.TokenBlacklistService tokenBlacklistService;

    @MockBean
    private AuthService authService;

    private RegisterRequest registerRequest;
    private AdminRegisterRequest adminRegisterRequest;
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private AuthResponse authResponse;
    private UserDTO userDTO;

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

        adminRegisterRequest = AdminRegisterRequest.builder()
                .userName("adminuser")
                .email("admin@example.com")
                .password("password123")
                .firstName("Admin")
                .lastName("User")
                .globalRoleIds(Collections.singletonList(2L))
                .build();

        loginRequest = LoginRequest.builder()
                .usernameOrEmail("testuser")
                .password("password123")
                .build();

        refreshTokenRequest = new RefreshTokenRequest("refreshToken123");

        userDTO = UserDTO.builder()
                .id(1L)
                .userName("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .phone("+1234567890")
                .isActive(true)
                .build();

        authResponse = AuthResponse.builder()
                .accessToken("accessToken123")
                .refreshToken("refreshToken123")
                .expiresIn(3600000L)
                .user(userDTO)
                .build();
    }

    @Test
    void register_ValidRequest_ReturnsCreated() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("accessToken123"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken123"))
                .andExpect(jsonPath("$.expiresIn").value(3600000L))
                .andExpect(jsonPath("$.user.userName").value("testuser"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void register_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Given - Invalid request with short username and invalid email
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .userName("ab")
                .email("invalid-email")
                .password("123")
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void registerAdmin_WithSuperadminRole_ReturnsCreated() throws Exception {
        // Given
        when(authService.registerAdmin(any(AdminRegisterRequest.class))).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(post("/api/auth/register-admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRegisterRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(authService, times(1)).registerAdmin(any(AdminRegisterRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerAdmin_WithAdminRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/register-admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRegisterRequest)))
                .andExpect(status().isForbidden());

        verify(authService, never()).registerAdmin(any(AdminRegisterRequest.class));
    }

    @Test
    void registerAdmin_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/register-admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRegisterRequest)))
                .andExpect(status().isForbidden());

        verify(authService, never()).registerAdmin(any(AdminRegisterRequest.class));
    }

    @Test
    void login_ValidCredentials_ReturnsOk() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken123"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken123"))
                .andExpect(jsonPath("$.user.userName").value("testuser"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void login_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Given - Missing password
        LoginRequest invalidRequest = LoginRequest.builder()
                .usernameOrEmail("testuser")
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(LoginRequest.class));
    }

    @Test
    void refreshToken_ValidToken_ReturnsOk() throws Exception {
        // Given
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken123"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken123"));

        verify(authService, times(1)).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    void refreshToken_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Given - Missing token
        RefreshTokenRequest invalidRequest = new RefreshTokenRequest(null);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCurrentUser_WithAuthentication_ReturnsOk() throws Exception {
        // Given
        when(authService.getCurrentUser(anyString())).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(get("/api/auth/me")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(authService, times(1)).getCurrentUser("testuser");
    }

    @Test
    void getCurrentUser_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/me")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(authService, never()).getCurrentUser(anyString());
    }

    @Test
    @WithMockUser(username = "testuser")
    void logout_WithAuthentication_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(authService).logout(anyString());

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .with(csrf())
                        .header("Authorization", "Bearer testAccessToken"))
                .andExpect(status().isNoContent());

        verify(authService, times(1)).logout("testAccessToken");
    }

    @Test
    @WithMockUser(username = "testuser")
    void logout_WithoutAuthorizationHeader_ReturnsNoContent() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(authService, never()).logout(anyString());
    }

    @Test
    void logout_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(authService, never()).logout(anyString());
    }
}
