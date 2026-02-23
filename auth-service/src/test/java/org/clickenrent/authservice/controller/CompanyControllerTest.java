package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.CompanyDTO;
import org.clickenrent.authservice.config.SecurityConfig;
import org.clickenrent.authservice.service.CompanyService;
import org.clickenrent.authservice.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
 * Unit tests for CompanyController.
 */
@WebMvcTest(CompanyController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class CompanyControllerTest {

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
    private CompanyService companyService;

    @MockBean
    private org.clickenrent.authservice.service.SecurityService securityService;

    @MockBean(name = "resourceSecurity")
    private org.clickenrent.authservice.security.ResourceSecurityExpression resourceSecurity;

    private CompanyDTO companyDTO;

    @BeforeEach
    void setUp() {
        companyDTO = CompanyDTO.builder()
                .id(1L)
                .externalId("comp-ext-123")
                .name("Test Hotel")
                .description("A wonderful test hotel")
                .website("https://testhotel.com")
                .logo("https://testhotel.com/logo.png")
                .erpPartnerId("ERP-001")
                .companyTypeId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllCompanies_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        Page<CompanyDTO> page = new PageImpl<>(Collections.singletonList(companyDTO));
        when(companyService.getAllCompanies(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/companies")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Test Hotel"))
                .andExpect(jsonPath("$.content[0].website").value("https://testhotel.com"));

        verify(companyService, times(1)).getAllCompanies(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCompanies_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        Page<CompanyDTO> page = new PageImpl<>(Collections.singletonList(companyDTO));
        when(companyService.getAllCompanies(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/companies")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(companyService, times(1)).getAllCompanies(any());
    }

    @Test
    @WithMockUser(roles = "B2B")
    void getAllCompanies_WithB2BRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/companies")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(companyService, never()).getAllCompanies(any());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllCompanies_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/companies")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(companyService, never()).getAllCompanies(any());
    }

    @Test
    void getAllCompanies_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/companies")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(companyService, never()).getAllCompanies(any());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getCompanyById_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        when(companyService.getCompanyById(1L)).thenReturn(companyDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/companies/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Hotel"))
                .andExpect(jsonPath("$.description").value("A wonderful test hotel"));

        verify(companyService, times(1)).getCompanyById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCompanyById_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(companyService.getCompanyById(1L)).thenReturn(companyDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/companies/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(companyService, times(1)).getCompanyById(1L);
    }

    @Test
    void getCompanyById_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/companies/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(companyService, never()).getCompanyById(anyLong());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createCompany_WithValidRequest_ReturnsCreated() throws Exception {
        // Given
        CompanyDTO newCompany = CompanyDTO.builder()
                .name("New Hotel")
                .description("Brand new hotel")
                .companyTypeId(1L)
                .build();

        CompanyDTO createdCompany = CompanyDTO.builder()
                .id(2L)
                .name("New Hotel")
                .description("Brand new hotel")
                .companyTypeId(1L)
                .build();

        when(companyService.createCompany(any(CompanyDTO.class))).thenReturn(createdCompany);

        // When & Then
        mockMvc.perform(post("/api/v1/companies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompany)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("New Hotel"));

        verify(companyService, times(1)).createCompany(any(CompanyDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCompany_WithAdminRole_ReturnsCreated() throws Exception {
        // Given
        CompanyDTO newCompany = CompanyDTO.builder()
                .name("New Hotel")
                .companyTypeId(1L)
                .build();

        when(companyService.createCompany(any(CompanyDTO.class))).thenReturn(newCompany);

        // When & Then
        mockMvc.perform(post("/api/v1/companies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompany)))
                .andExpect(status().isCreated());

        verify(companyService, times(1)).createCompany(any(CompanyDTO.class));
    }

    @Test
    @WithMockUser(roles = "B2B")
    void createCompany_WithB2BRole_ReturnsForbidden() throws Exception {
        // Given
        CompanyDTO newCompany = CompanyDTO.builder()
                .name("New Hotel")
                .companyTypeId(1L)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/companies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompany)))
                .andExpect(status().isForbidden());

        verify(companyService, never()).createCompany(any(CompanyDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createCompany_WithInvalidRequest_ReturnsBadRequest() throws Exception {
        // Given - Missing required name field
        CompanyDTO invalidCompany = CompanyDTO.builder()
                .description("No name provided")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/companies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCompany)))
                .andExpect(status().isBadRequest());

        verify(companyService, never()).createCompany(any(CompanyDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateCompany_WithValidRequest_ReturnsOk() throws Exception {
        // Given
        CompanyDTO updatedCompany = CompanyDTO.builder()
                .id(1L)
                .name("Updated Hotel")
                .description("Updated description")
                .companyTypeId(1L)
                .build();

        when(companyService.updateCompany(eq(1L), any(CompanyDTO.class))).thenReturn(updatedCompany);

        // When & Then
        mockMvc.perform(put("/api/v1/companies/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Hotel"))
                .andExpect(jsonPath("$.description").value("Updated description"));

        verify(companyService, times(1)).updateCompany(eq(1L), any(CompanyDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCompany_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(companyService.updateCompany(eq(1L), any(CompanyDTO.class))).thenReturn(companyDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/companies/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyDTO)))
                .andExpect(status().isOk());

        verify(companyService, times(1)).updateCompany(eq(1L), any(CompanyDTO.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateCompany_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/companies/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyDTO)))
                .andExpect(status().isForbidden());

        verify(companyService, never()).updateCompany(anyLong(), any(CompanyDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteCompany_WithSuperadminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(companyService).deleteCompany(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/companies/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(companyService, times(1)).deleteCompany(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCompany_WithAdminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(companyService).deleteCompany(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/companies/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(companyService, times(1)).deleteCompany(1L);
    }

    @Test
    @WithMockUser(roles = "B2B")
    void deleteCompany_WithB2BRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/companies/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(companyService, never()).deleteCompany(anyLong());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deleteCompany_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/companies/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(companyService, never()).deleteCompany(anyLong());
    }
}
