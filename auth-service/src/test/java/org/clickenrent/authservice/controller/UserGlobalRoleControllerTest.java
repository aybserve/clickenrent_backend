package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.AssignGlobalRoleRequest;
import org.clickenrent.authservice.dto.UserGlobalRoleDTO;
import org.clickenrent.authservice.service.UserGlobalRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserGlobalRoleController.
 */
@WebMvcTest(UserGlobalRoleController.class)
@AutoConfigureMockMvc
class UserGlobalRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserGlobalRoleService userGlobalRoleService;

    private UserGlobalRoleDTO userGlobalRoleDTO;
    private AssignGlobalRoleRequest assignRequest;

    @BeforeEach
    void setUp() {
        userGlobalRoleDTO = UserGlobalRoleDTO.builder()
                .id(1L)
                .userId(1L)
                .globalRoleId(2L)
                .build();

        assignRequest = AssignGlobalRoleRequest.builder()
                .userId(1L)
                .globalRoleId(2L)
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void assignGlobalRoleToUser_WithSuperadminRole_ReturnsCreated() throws Exception {
        // Given
        when(userGlobalRoleService.assignGlobalRoleToUser(1L, 2L)).thenReturn(userGlobalRoleDTO);

        // When & Then
        mockMvc.perform(post("/api/user-global-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.globalRoleId").value(2L));

        verify(userGlobalRoleService, times(1)).assignGlobalRoleToUser(1L, 2L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignGlobalRoleToUser_WithAdminRole_ReturnsCreated() throws Exception {
        // Given
        when(userGlobalRoleService.assignGlobalRoleToUser(anyLong(), anyLong())).thenReturn(userGlobalRoleDTO);

        // When & Then
        mockMvc.perform(post("/api/user-global-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isCreated());

        verify(userGlobalRoleService, times(1)).assignGlobalRoleToUser(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "B2B")
    void assignGlobalRoleToUser_WithB2BRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/user-global-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isForbidden());

        verify(userGlobalRoleService, never()).assignGlobalRoleToUser(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void assignGlobalRoleToUser_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/user-global-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isForbidden());

        verify(userGlobalRoleService, never()).assignGlobalRoleToUser(anyLong(), anyLong());
    }

    @Test
    void assignGlobalRoleToUser_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/user-global-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isForbidden());

        verify(userGlobalRoleService, never()).assignGlobalRoleToUser(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void assignGlobalRoleToUser_WithInvalidRequest_ReturnsBadRequest() throws Exception {
        // Given - Invalid request with null userId
        AssignGlobalRoleRequest invalidRequest = AssignGlobalRoleRequest.builder()
                .globalRoleId(2L)
                .build();

        // When & Then
        mockMvc.perform(post("/api/user-global-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userGlobalRoleService, never()).assignGlobalRoleToUser(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getUserGlobalRoles_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        List<UserGlobalRoleDTO> roles = Collections.singletonList(userGlobalRoleDTO);
        when(userGlobalRoleService.getUserGlobalRoles(1L)).thenReturn(roles);

        // When & Then
        mockMvc.perform(get("/api/user-global-roles/user/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].globalRoleId").value(2L));

        verify(userGlobalRoleService, times(1)).getUserGlobalRoles(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserGlobalRoles_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        List<UserGlobalRoleDTO> roles = Collections.singletonList(userGlobalRoleDTO);
        when(userGlobalRoleService.getUserGlobalRoles(1L)).thenReturn(roles);

        // When & Then
        mockMvc.perform(get("/api/user-global-roles/user/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userGlobalRoleService, times(1)).getUserGlobalRoles(1L);
    }

    @Test
    @WithMockUser(roles = "B2B")
    void getUserGlobalRoles_WithB2BRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user-global-roles/user/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userGlobalRoleService, never()).getUserGlobalRoles(anyLong());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getUserGlobalRoles_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user-global-roles/user/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userGlobalRoleService, never()).getUserGlobalRoles(anyLong());
    }

    @Test
    void getUserGlobalRoles_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user-global-roles/user/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userGlobalRoleService, never()).getUserGlobalRoles(anyLong());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void removeGlobalRoleFromUser_WithSuperadminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(userGlobalRoleService).removeGlobalRoleFromUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/user-global-roles/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userGlobalRoleService, times(1)).removeGlobalRoleFromUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeGlobalRoleFromUser_WithAdminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(userGlobalRoleService).removeGlobalRoleFromUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/user-global-roles/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userGlobalRoleService, times(1)).removeGlobalRoleFromUser(1L);
    }

    @Test
    @WithMockUser(roles = "B2B")
    void removeGlobalRoleFromUser_WithB2BRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/user-global-roles/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userGlobalRoleService, never()).removeGlobalRoleFromUser(anyLong());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void removeGlobalRoleFromUser_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/user-global-roles/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userGlobalRoleService, never()).removeGlobalRoleFromUser(anyLong());
    }

    @Test
    void removeGlobalRoleFromUser_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/user-global-roles/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userGlobalRoleService, never()).removeGlobalRoleFromUser(anyLong());
    }
}
