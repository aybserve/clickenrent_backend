package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.*;
import org.clickenrent.authservice.config.SecurityConfig;
import org.clickenrent.authservice.service.UserCompanyService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserCompanyController.
 */
@WebMvcTest(UserCompanyController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class UserCompanyControllerTest {

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
    private UserCompanyService userCompanyService;

    private UserCompanyDTO userCompanyDTO;
    private UserCompanyDetailDTO userCompanyDetailDTO;
    private AssignUserToCompanyRequest assignRequest;
    private UpdateUserCompanyRoleRequest updateRoleRequest;

    @BeforeEach
    void setUp() {
        userCompanyDTO = UserCompanyDTO.builder()
                .id(1L)
                .userId(1L)
                .companyId(1L)
                .companyRoleId(1L)
                .build();

        UserDTO user = UserDTO.builder()
                .id(1L)
                .userName("testuser")
                .email("test@example.com")
                .build();

        CompanyDTO company = CompanyDTO.builder()
                .id(1L)
                .name("Test Hotel")
                .build();

        CompanyRoleDTO companyRole = CompanyRoleDTO.builder()
                .id(1L)
                .name("Owner")
                .build();

        userCompanyDetailDTO = UserCompanyDetailDTO.builder()
                .id(1L)
                .user(user)
                .company(company)
                .companyRole(companyRole)
                .build();

        assignRequest = AssignUserToCompanyRequest.builder()
                .userId(1L)
                .companyId(1L)
                .companyRoleId(1L)
                .build();

        updateRoleRequest = UpdateUserCompanyRoleRequest.builder()
                .companyRoleId(2L)
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void assignUserToCompany_WithSuperadminRole_ReturnsCreated() throws Exception {
        // Given
        when(userCompanyService.assignUserToCompany(1L, 1L, 1L)).thenReturn(userCompanyDTO);

        // When & Then
        mockMvc.perform(post("/api/user-companies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.companyId").value(1L))
                .andExpect(jsonPath("$.companyRoleId").value(1L));

        verify(userCompanyService, times(1)).assignUserToCompany(1L, 1L, 1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignUserToCompany_WithAdminRole_ReturnsCreated() throws Exception {
        // Given
        when(userCompanyService.assignUserToCompany(anyLong(), anyLong(), anyLong())).thenReturn(userCompanyDTO);

        // When & Then
        mockMvc.perform(post("/api/user-companies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isCreated());

        verify(userCompanyService, times(1)).assignUserToCompany(anyLong(), anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "B2B")
    void assignUserToCompany_WithB2BRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/user-companies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isForbidden());

        verify(userCompanyService, never()).assignUserToCompany(anyLong(), anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void assignUserToCompany_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/user-companies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isForbidden());

        verify(userCompanyService, never()).assignUserToCompany(anyLong(), anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void assignUserToCompany_WithInvalidRequest_ReturnsBadRequest() throws Exception {
        // Given - Invalid request with null userId
        AssignUserToCompanyRequest invalidRequest = AssignUserToCompanyRequest.builder()
                .companyId(1L)
                .companyRoleId(1L)
                .build();

        // When & Then
        mockMvc.perform(post("/api/user-companies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userCompanyService, never()).assignUserToCompany(anyLong(), anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getUserCompanies_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        List<UserCompanyDetailDTO> companies = Collections.singletonList(userCompanyDetailDTO);
        when(userCompanyService.getUserCompanies(1L)).thenReturn(companies);

        // When & Then
        mockMvc.perform(get("/api/user-companies/user/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].user.userName").value("testuser"))
                .andExpect(jsonPath("$[0].company.name").value("Test Hotel"));

        verify(userCompanyService, times(1)).getUserCompanies(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserCompanies_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        List<UserCompanyDetailDTO> companies = Collections.singletonList(userCompanyDetailDTO);
        when(userCompanyService.getUserCompanies(1L)).thenReturn(companies);

        // When & Then
        mockMvc.perform(get("/api/user-companies/user/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userCompanyService, times(1)).getUserCompanies(1L);
    }

    @Test
    void getUserCompanies_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user-companies/user/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userCompanyService, never()).getUserCompanies(anyLong());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getCompanyUsers_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        List<UserCompanyDetailDTO> users = Collections.singletonList(userCompanyDetailDTO);
        when(userCompanyService.getCompanyUsers(1L)).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/user-companies/company/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].user.userName").value("testuser"))
                .andExpect(jsonPath("$[0].company.name").value("Test Hotel"));

        verify(userCompanyService, times(1)).getCompanyUsers(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCompanyUsers_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        List<UserCompanyDetailDTO> users = Collections.singletonList(userCompanyDetailDTO);
        when(userCompanyService.getCompanyUsers(1L)).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/user-companies/company/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userCompanyService, times(1)).getCompanyUsers(1L);
    }

    @Test
    void getCompanyUsers_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user-companies/company/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userCompanyService, never()).getCompanyUsers(anyLong());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateUserCompanyRole_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        UserCompanyDTO updatedUserCompany = UserCompanyDTO.builder()
                .id(1L)
                .userId(1L)
                .companyId(1L)
                .companyRoleId(2L)
                .build();

        when(userCompanyService.updateUserCompanyRole(1L, 2L)).thenReturn(updatedUserCompany);

        // When & Then
        mockMvc.perform(put("/api/user-companies/1/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRoleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.companyRoleId").value(2L));

        verify(userCompanyService, times(1)).updateUserCompanyRole(1L, 2L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserCompanyRole_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(userCompanyService.updateUserCompanyRole(anyLong(), anyLong())).thenReturn(userCompanyDTO);

        // When & Then
        mockMvc.perform(put("/api/user-companies/1/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRoleRequest)))
                .andExpect(status().isOk());

        verify(userCompanyService, times(1)).updateUserCompanyRole(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateUserCompanyRole_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/user-companies/1/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRoleRequest)))
                .andExpect(status().isForbidden());

        verify(userCompanyService, never()).updateUserCompanyRole(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void removeUserFromCompany_WithSuperadminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(userCompanyService).removeUserFromCompany(1L);

        // When & Then
        mockMvc.perform(delete("/api/user-companies/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userCompanyService, times(1)).removeUserFromCompany(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeUserFromCompany_WithAdminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(userCompanyService).removeUserFromCompany(1L);

        // When & Then
        mockMvc.perform(delete("/api/user-companies/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userCompanyService, times(1)).removeUserFromCompany(1L);
    }

    @Test
    @WithMockUser(roles = "B2B")
    void removeUserFromCompany_WithB2BRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/user-companies/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userCompanyService, never()).removeUserFromCompany(anyLong());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void removeUserFromCompany_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/user-companies/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userCompanyService, never()).removeUserFromCompany(anyLong());
    }
}
