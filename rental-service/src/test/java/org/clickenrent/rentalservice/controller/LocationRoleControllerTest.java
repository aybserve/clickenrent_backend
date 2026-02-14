package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.LocationRoleDTO;
import org.clickenrent.rentalservice.service.LocationRoleService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationRoleController.class)
@AutoConfigureMockMvc
class LocationRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocationRoleService locationRoleService;

    private LocationRoleDTO roleDTO;

    @BeforeEach
    void setUp() {
        roleDTO = LocationRoleDTO.builder()
                .id(1L)
                .name("Admin")
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllLocationRoles_ReturnsOk() throws Exception {
        when(locationRoleService.getAllRoles()).thenReturn(Arrays.asList(roleDTO));

        mockMvc.perform(get("/api/location-roles").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Admin"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLocationRoleById_ReturnsOk() throws Exception {
        when(locationRoleService.getRoleById(1L)).thenReturn(roleDTO);

        mockMvc.perform(get("/api/location-roles/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Admin"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void createLocationRole_ReturnsCreated() throws Exception {
        when(locationRoleService.createLocationRole(any())).thenReturn(roleDTO);

        mockMvc.perform(post("/api/location-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateLocationRole_ReturnsOk() throws Exception {
        when(locationRoleService.updateLocationRole(eq(1L), any())).thenReturn(roleDTO);

        mockMvc.perform(put("/api/location-roles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDTO)))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteLocationRole_ReturnsNoContent() throws Exception {
        doNothing().when(locationRoleService).deleteLocationRole(1L);

        mockMvc.perform(delete("/api/location-roles/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}