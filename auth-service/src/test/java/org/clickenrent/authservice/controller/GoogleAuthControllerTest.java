package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.AuthResponse;
import org.clickenrent.authservice.dto.GoogleLoginRequest;
import org.clickenrent.authservice.dto.UserDTO;
import org.clickenrent.authservice.exception.UnauthorizedException;
import org.clickenrent.authservice.service.GoogleOAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for GoogleAuthController.
 * Tests the REST API endpoints for Google OAuth authentication.
 */
@WebMvcTest(GoogleAuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for unit tests
class GoogleAuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private GoogleOAuthService googleOAuthService;
    
    private static final String GOOGLE_LOGIN_ENDPOINT = "/api/auth/google/login";
    private static final String TEST_CODE = "test-auth-code";
    private static final String TEST_REDIRECT_URI = "http://localhost:3000/callback";
    
    @Test
    void loginWithGoogle_ValidRequest_ReturnsAuthResponse() throws Exception {
        // Arrange
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .code(TEST_CODE)
                .redirectUri(TEST_REDIRECT_URI)
                .build();
        
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .userName("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();
        
        AuthResponse authResponse = new AuthResponse(
                "jwt-access-token",
                "jwt-refresh-token",
                3600000L,
                userDTO
        );
        
        when(googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI))
                .thenReturn(authResponse);
        
        // Act & Assert
        mockMvc.perform(post(GOOGLE_LOGIN_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("jwt-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("jwt-refresh-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600000L))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }
    
    @Test
    void loginWithGoogle_MissingCode_ReturnsBadRequest() throws Exception {
        // Arrange
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .redirectUri(TEST_REDIRECT_URI)
                // code is missing
                .build();
        
        // Act & Assert
        mockMvc.perform(post(GOOGLE_LOGIN_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void loginWithGoogle_MissingRedirectUri_ReturnsBadRequest() throws Exception {
        // Arrange
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .code(TEST_CODE)
                // redirectUri is missing
                .build();
        
        // Act & Assert
        mockMvc.perform(post(GOOGLE_LOGIN_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void loginWithGoogle_EmptyCode_ReturnsBadRequest() throws Exception {
        // Arrange
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .code("")
                .redirectUri(TEST_REDIRECT_URI)
                .build();
        
        // Act & Assert
        mockMvc.perform(post(GOOGLE_LOGIN_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void loginWithGoogle_InvalidAuthCode_ReturnsUnauthorized() throws Exception {
        // Arrange
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .code("invalid-code")
                .redirectUri(TEST_REDIRECT_URI)
                .build();
        
        when(googleOAuthService.authenticateWithGoogle(anyString(), anyString()))
                .thenThrow(new UnauthorizedException("Failed to authenticate with Google"));
        
        // Act & Assert
        mockMvc.perform(post(GOOGLE_LOGIN_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void loginWithGoogle_ServiceThrowsException_ReturnsUnauthorized() throws Exception {
        // Arrange
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .code(TEST_CODE)
                .redirectUri(TEST_REDIRECT_URI)
                .build();
        
        when(googleOAuthService.authenticateWithGoogle(TEST_CODE, TEST_REDIRECT_URI))
                .thenThrow(new UnauthorizedException("Google OAuth service error"));
        
        // Act & Assert
        mockMvc.perform(post(GOOGLE_LOGIN_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void loginWithGoogle_InvalidJson_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post(GOOGLE_LOGIN_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void loginWithGoogle_EmptyBody_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post(GOOGLE_LOGIN_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}

