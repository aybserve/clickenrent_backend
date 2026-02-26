package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.CreateUserRequest;
import org.clickenrent.authservice.config.SecurityConfig;
import org.clickenrent.authservice.dto.UserDTO;
import org.clickenrent.authservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController.
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class UserControllerTest {

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
    private UserService userService;

    @MockBean
    private org.clickenrent.authservice.service.UserStatisticsService userStatisticsService;

    @MockBean
    private org.clickenrent.authservice.service.SecurityService securityService;

    @MockBean(name = "resourceSecurity")
    private org.clickenrent.authservice.security.ResourceSecurityExpression resourceSecurity;

    private UserDTO userDTO;
    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setUp() {
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
                .build();

        createUserRequest = CreateUserRequest.builder()
                .userName("newuser")
                .email("new@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .phone("+9876543210")
                .languageId(1L)
                .isActive(true)
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllUsers_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        Page<UserDTO> page = new PageImpl<>(Collections.singletonList(userDTO));
        when(userService.getAllUsers(any(PageRequest.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/users")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].userName").value("testuser"))
                .andExpect(jsonPath("$.content[0].email").value("test@example.com"));

        verify(userService, times(1)).getAllUsers(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        Page<UserDTO> page = new PageImpl<>(Collections.singletonList(userDTO));
        when(userService.getAllUsers(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/users")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userService, times(1)).getAllUsers(any());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllUsers_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userService, never()).getAllUsers(any());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getUserById_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/users/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/users/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserById_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getUserByExternalId_ReturnsOk() throws Exception {
        // Given
        when(userService.findByExternalId("ext-123")).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/users/external/ext-123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("ext-123"))
                .andExpect(jsonPath("$.userName").value("testuser"));

        verify(userService, times(1)).findByExternalId("ext-123");
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createUser_WithValidRequest_ReturnsCreated() throws Exception {
        // Given
        UserDTO createdUser = UserDTO.builder()
                .id(2L)
                .userName("newuser")
                .email("new@example.com")
                .firstName("New")
                .lastName("User")
                .isActive(true)
                .build();

        when(userService.createUser(any(UserDTO.class), anyString())).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.userName").value("newuser"))
                .andExpect(jsonPath("$.email").value("new@example.com"));

        verify(userService, times(1)).createUser(any(UserDTO.class), eq("password123"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_WithAdminRole_ReturnsCreated() throws Exception {
        // Given
        UserDTO createdUser = UserDTO.builder()
                .id(2L)
                .userName("newuser")
                .email("new@example.com")
                .build();

        when(userService.createUser(any(UserDTO.class), anyString())).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).createUser(any(UserDTO.class), anyString());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createUser_WithCustomerRole_ReturnsCreated() throws Exception {
        // Given
        UserDTO createdUser = UserDTO.builder()
                .id(2L)
                .userName("newuser")
                .email("new@example.com")
                .build();

        when(userService.createUser(any(UserDTO.class), anyString())).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).createUser(any(UserDTO.class), anyString());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createUser_WithInvalidRequest_ReturnsBadRequest() throws Exception {
        // Given - Invalid request with short password
        CreateUserRequest invalidRequest = CreateUserRequest.builder()
                .userName("newuser")
                .email("invalid-email")
                .password("123")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserDTO.class), anyString());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateUser_WithValidRequest_ReturnsOk() throws Exception {
        // Given
        UserDTO updatedUser = UserDTO.builder()
                .id(1L)
                .userName("testuser")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .isActive(true)
                .build();

        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/v1/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.firstName").value("Updated"));

        verify(userService, times(1)).updateUser(eq(1L), any(UserDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk());

        verify(userService, times(1)).updateUser(eq(1L), any(UserDTO.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateUser_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isForbidden());

        verify(userService, never()).updateUser(anyLong(), any(UserDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteUser_WithSuperadminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_WithAdminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deleteUser_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/users/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userService, never()).deleteUser(anyLong());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void activateUser_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        UserDTO activatedUser = UserDTO.builder()
                .id(1L)
                .userName("testuser")
                .email("test@example.com")
                .isActive(true)
                .build();

        when(userService.activateUser(1L)).thenReturn(activatedUser);

        // When & Then
        mockMvc.perform(put("/api/v1/users/1/activate")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(true));

        verify(userService, times(1)).activateUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void activateUser_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(userService.activateUser(1L)).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/users/1/activate")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userService, times(1)).activateUser(1L);
    }

    @Test
    @WithMockUser(roles = "B2B")
    void activateUser_WithB2BRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/users/1/activate")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userService, never()).activateUser(anyLong());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deactivateUser_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        UserDTO deactivatedUser = UserDTO.builder()
                .id(1L)
                .userName("testuser")
                .email("test@example.com")
                .isActive(false)
                .build();

        when(userService.deactivateUser(1L)).thenReturn(deactivatedUser);

        // When & Then
        mockMvc.perform(put("/api/v1/users/1/deactivate")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));

        verify(userService, times(1)).deactivateUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deactivateUser_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(userService.deactivateUser(1L)).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/users/1/deactivate")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userService, times(1)).deactivateUser(1L);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deactivateUser_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/users/1/deactivate")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userService, never()).deactivateUser(anyLong());
    }
}
