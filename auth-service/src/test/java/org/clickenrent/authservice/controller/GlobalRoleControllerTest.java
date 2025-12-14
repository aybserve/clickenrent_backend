package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.GlobalRoleDTO;
import org.clickenrent.authservice.service.GlobalRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GlobalRoleController.class)
@AutoConfigureMockMvc
class GlobalRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GlobalRoleService globalRoleService;

    private GlobalRoleDTO globalRoleDTO;

    @BeforeEach
    void setUp() {
        globalRoleDTO = GlobalRoleDTO.builder()
                .id(1L)
                .name("SUPERADMIN")
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllGlobalRoles_WithSuperadminRole_ReturnsOk() throws Exception {
        List<GlobalRoleDTO> roles = Arrays.asList(globalRoleDTO);
        when(globalRoleService.getAllGlobalRoles()).thenReturn(roles);

        mockMvc.perform(get("/api/global-roles").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("SUPERADMIN"));

        verify(globalRoleService, times(1)).getAllGlobalRoles();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllGlobalRoles_WithAdminRole_ReturnsOk() throws Exception {
        List<GlobalRoleDTO> roles = Arrays.asList(globalRoleDTO);
        when(globalRoleService.getAllGlobalRoles()).thenReturn(roles);

        mockMvc.perform(get("/api/global-roles").with(csrf()))
                .andExpect(status().isOk());

        verify(globalRoleService, times(1)).getAllGlobalRoles();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllGlobalRoles_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/global-roles").with(csrf()))
                .andExpect(status().isForbidden());

        verify(globalRoleService, never()).getAllGlobalRoles();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getGlobalRoleById_ReturnsOk() throws Exception {
        when(globalRoleService.getGlobalRoleById(1L)).thenReturn(globalRoleDTO);

        mockMvc.perform(get("/api/global-roles/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("SUPERADMIN"));

        verify(globalRoleService, times(1)).getGlobalRoleById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createGlobalRole_WithValidRequest_ReturnsCreated() throws Exception {
        GlobalRoleDTO newRole = GlobalRoleDTO.builder().name("MANAGER").build();
        GlobalRoleDTO createdRole = GlobalRoleDTO.builder().id(2L).name("MANAGER").build();

        when(globalRoleService.createGlobalRole(any(GlobalRoleDTO.class))).thenReturn(createdRole);

        mockMvc.perform(post("/api/global-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRole)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("MANAGER"));

        verify(globalRoleService, times(1)).createGlobalRole(any(GlobalRoleDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateGlobalRole_ReturnsOk() throws Exception {
        GlobalRoleDTO updated = GlobalRoleDTO.builder().id(1L).name("SUPERADMIN_UPDATED").build();

        when(globalRoleService.updateGlobalRole(eq(1L), any(GlobalRoleDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/global-roles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(globalRoleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SUPERADMIN_UPDATED"));

        verify(globalRoleService, times(1)).updateGlobalRole(eq(1L), any(GlobalRoleDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteGlobalRole_ReturnsNoContent() throws Exception {
        doNothing().when(globalRoleService).deleteGlobalRole(1L);

        mockMvc.perform(delete("/api/global-roles/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(globalRoleService, times(1)).deleteGlobalRole(1L);
    }
}
