package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.AppleLoginRequest;
import org.clickenrent.authservice.dto.AuthResponse;
import org.clickenrent.authservice.dto.UserDTO;
import org.clickenrent.authservice.exception.UnauthorizedException;
import org.clickenrent.authservice.service.AppleOAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AppleAuthController.
 */
@WebMvcTest(AppleAuthController.class)
class AppleAuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private AppleOAuthService appleOAuthService;
    
    @Test
    @WithMockUser
    void testLoginWithApple_Success() throws Exception {
        // Given
        AppleLoginRequest request = AppleLoginRequest.builder()
                .code("test-auth-code")
                .redirectUri("http://localhost:3000/auth/apple/callback")
                .build();
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setUserName("testuser");
        
        AuthResponse authResponse = new AuthResponse(
                "access-token",
                "refresh-token",
                86400000L,
                userDTO
        );
        
        when(appleOAuthService.authenticateWithApple(anyString(), anyString()))
                .thenReturn(authResponse);
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/apple/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
        
        verify(appleOAuthService).authenticateWithApple("test-auth-code", "http://localhost:3000/auth/apple/callback");
    }
    
    @Test
    @WithMockUser
    void testLoginWithApple_InvalidRequest_MissingCode() throws Exception {
        // Given: Request without code
        AppleLoginRequest request = AppleLoginRequest.builder()
                .redirectUri("http://localhost:3000/auth/apple/callback")
                .build();
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/apple/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(appleOAuthService, never()).authenticateWithApple(anyString(), anyString());
    }
    
    @Test
    @WithMockUser
    void testLoginWithApple_InvalidRequest_MissingRedirectUri() throws Exception {
        // Given: Request without redirectUri
        AppleLoginRequest request = AppleLoginRequest.builder()
                .code("test-auth-code")
                .build();
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/apple/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(appleOAuthService, never()).authenticateWithApple(anyString(), anyString());
    }
    
    @Test
    @WithMockUser
    void testLoginWithApple_ServiceThrowsException() throws Exception {
        // Given
        AppleLoginRequest request = AppleLoginRequest.builder()
                .code("invalid-code")
                .redirectUri("http://localhost:3000/auth/apple/callback")
                .build();
        
        when(appleOAuthService.authenticateWithApple(anyString(), anyString()))
                .thenThrow(new UnauthorizedException("Invalid Apple authorization code"));
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/apple/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
        
        verify(appleOAuthService).authenticateWithApple("invalid-code", "http://localhost:3000/auth/apple/callback");
    }
}
