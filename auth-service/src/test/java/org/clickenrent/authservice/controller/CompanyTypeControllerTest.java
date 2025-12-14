package org.clickenrent.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.authservice.dto.CompanyTypeDTO;
import org.clickenrent.authservice.service.CompanyTypeService;
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

@WebMvcTest(CompanyTypeController.class)
@AutoConfigureMockMvc
class CompanyTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompanyTypeService companyTypeService;

    private CompanyTypeDTO companyTypeDTO;

    @BeforeEach
    void setUp() {
        companyTypeDTO = CompanyTypeDTO.builder()
                .id(1L)
                .name("Hotel")
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllCompanyTypes_WithSuperadminRole_ReturnsOk() throws Exception {
        List<CompanyTypeDTO> types = Arrays.asList(companyTypeDTO);
        when(companyTypeService.getAllCompanyTypes()).thenReturn(types);

        mockMvc.perform(get("/api/company-types").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Hotel"));

        verify(companyTypeService, times(1)).getAllCompanyTypes();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCompanyTypes_WithAdminRole_ReturnsOk() throws Exception {
        List<CompanyTypeDTO> types = Arrays.asList(companyTypeDTO);
        when(companyTypeService.getAllCompanyTypes()).thenReturn(types);

        mockMvc.perform(get("/api/company-types").with(csrf()))
                .andExpect(status().isOk());

        verify(companyTypeService, times(1)).getAllCompanyTypes();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllCompanyTypes_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/company-types").with(csrf()))
                .andExpect(status().isForbidden());

        verify(companyTypeService, never()).getAllCompanyTypes();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCompanyTypeById_ReturnsOk() throws Exception {
        when(companyTypeService.getCompanyTypeById(1L)).thenReturn(companyTypeDTO);

        mockMvc.perform(get("/api/company-types/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Hotel"));

        verify(companyTypeService, times(1)).getCompanyTypeById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createCompanyType_WithValidRequest_ReturnsCreated() throws Exception {
        CompanyTypeDTO newType = CompanyTypeDTO.builder().name("B&B").build();
        CompanyTypeDTO createdType = CompanyTypeDTO.builder().id(2L).name("B&B").build();

        when(companyTypeService.createCompanyType(any(CompanyTypeDTO.class))).thenReturn(createdType);

        mockMvc.perform(post("/api/company-types")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newType)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("B&B"));

        verify(companyTypeService, times(1)).createCompanyType(any(CompanyTypeDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCompanyType_ReturnsOk() throws Exception {
        CompanyTypeDTO updated = CompanyTypeDTO.builder().id(1L).name("Hotel (Updated)").build();

        when(companyTypeService.updateCompanyType(eq(1L), any(CompanyTypeDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/company-types/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyTypeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hotel (Updated)"));

        verify(companyTypeService, times(1)).updateCompanyType(eq(1L), any(CompanyTypeDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteCompanyType_ReturnsNoContent() throws Exception {
        doNothing().when(companyTypeService).deleteCompanyType(1L);

        mockMvc.perform(delete("/api/company-types/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(companyTypeService, times(1)).deleteCompanyType(1L);
    }
}
