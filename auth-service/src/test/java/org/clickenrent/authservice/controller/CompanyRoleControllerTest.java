package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.CompanyRoleDTO;
import org.clickenrent.authservice.config.SecurityConfig;
import org.clickenrent.authservice.service.CompanyRoleService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyRoleController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class CompanyRoleControllerTest {

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
    private CompanyRoleService companyRoleService;

    private CompanyRoleDTO companyRoleDTO;

    @BeforeEach
    void setUp() {
        companyRoleDTO = CompanyRoleDTO.builder()
                .id(1L)
                .name("Owner")
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllCompanyRoles_WithSuperadminRole_ReturnsOk() throws Exception {
        List<CompanyRoleDTO> roles = Arrays.asList(companyRoleDTO);
        when(companyRoleService.getAllCompanyRoles()).thenReturn(roles);

        mockMvc.perform(get("/api/company-roles").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Owner"));

        verify(companyRoleService, times(1)).getAllCompanyRoles();
    }

    @Test
    @WithMockUser(roles = "B2B")
    void getAllCompanyRoles_WithB2BRole_ReturnsOk() throws Exception {
        List<CompanyRoleDTO> roles = Arrays.asList(companyRoleDTO);
        when(companyRoleService.getAllCompanyRoles()).thenReturn(roles);

        mockMvc.perform(get("/api/company-roles").with(csrf()))
                .andExpect(status().isOk());

        verify(companyRoleService, times(1)).getAllCompanyRoles();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllCompanyRoles_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/company-roles").with(csrf()))
                .andExpect(status().isForbidden());

        verify(companyRoleService, never()).getAllCompanyRoles();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCompanyRoleById_ReturnsOk() throws Exception {
        when(companyRoleService.getCompanyRoleById(1L)).thenReturn(companyRoleDTO);

        mockMvc.perform(get("/api/company-roles/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Owner"));

        verify(companyRoleService, times(1)).getCompanyRoleById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createCompanyRole_WithValidRequest_ReturnsCreated() throws Exception {
        CompanyRoleDTO newRole = CompanyRoleDTO.builder().name("Staff").build();
        CompanyRoleDTO createdRole = CompanyRoleDTO.builder().id(2L).name("Staff").build();

        when(companyRoleService.createCompanyRole(any(CompanyRoleDTO.class))).thenReturn(createdRole);

        mockMvc.perform(post("/api/company-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRole)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Staff"));

        verify(companyRoleService, times(1)).createCompanyRole(any(CompanyRoleDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCompanyRole_ReturnsOk() throws Exception {
        CompanyRoleDTO updated = CompanyRoleDTO.builder().id(1L).name("Owner (Updated)").build();

        when(companyRoleService.updateCompanyRole(eq(1L), any(CompanyRoleDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/company-roles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyRoleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Owner (Updated)"));

        verify(companyRoleService, times(1)).updateCompanyRole(eq(1L), any(CompanyRoleDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteCompanyRole_ReturnsNoContent() throws Exception {
        doNothing().when(companyRoleService).deleteCompanyRole(1L);

        mockMvc.perform(delete("/api/company-roles/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(companyRoleService, times(1)).deleteCompanyRole(1L);
    }
}
