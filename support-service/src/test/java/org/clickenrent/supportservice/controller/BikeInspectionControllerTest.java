package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.BikeInspectionDTO;
import org.clickenrent.supportservice.service.BikeInspectionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BikeInspectionController.class)
@AutoConfigureMockMvc
class BikeInspectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeInspectionService bikeInspectionService;

    private BikeInspectionDTO bikeInspectionDTO;

    @BeforeEach
    void setUp() {
        bikeInspectionDTO = BikeInspectionDTO.builder()
                .id(1L)
                .externalId("inspection-uuid-1")
                .userExternalId("user-uuid-1")
                .companyExternalId("company-uuid-1")
                .comment("Initial check")
                .bikeInspectionStatusId(1L)
                .bikeInspectionStatusName("PENDING")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(bikeInspectionService.getAll()).thenReturn(Arrays.asList(bikeInspectionDTO));

        mockMvc.perform(get("/api/v1/bike-inspections").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userExternalId").value("user-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(bikeInspectionService.getById(1L)).thenReturn(bikeInspectionDTO);

        mockMvc.perform(get("/api/v1/bike-inspections/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("inspection-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByExternalId_ReturnsOk() throws Exception {
        when(bikeInspectionService.getByExternalId("inspection-uuid-1")).thenReturn(bikeInspectionDTO);

        mockMvc.perform(get("/api/v1/bike-inspections/external/inspection-uuid-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("inspection-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByUserExternalId_ReturnsOk() throws Exception {
        when(bikeInspectionService.getByUserExternalId("user-uuid-1")).thenReturn(Arrays.asList(bikeInspectionDTO));

        mockMvc.perform(get("/api/v1/bike-inspections/user/user-uuid-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userExternalId").value("user-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByCompanyExternalId_ReturnsOk() throws Exception {
        when(bikeInspectionService.getByCompanyExternalId("company-uuid-1")).thenReturn(Arrays.asList(bikeInspectionDTO));

        mockMvc.perform(get("/api/v1/bike-inspections/company/company-uuid-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyExternalId").value("company-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByStatusId_ReturnsOk() throws Exception {
        when(bikeInspectionService.getByStatusId(1L)).thenReturn(Arrays.asList(bikeInspectionDTO));

        mockMvc.perform(get("/api/v1/bike-inspections/status/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bikeInspectionStatusId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(bikeInspectionService.create(any())).thenReturn(bikeInspectionDTO);

        mockMvc.perform(post("/api/v1/bike-inspections")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeInspectionDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(bikeInspectionService.update(eq(1L), any())).thenReturn(bikeInspectionDTO);

        mockMvc.perform(put("/api/v1/bike-inspections/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeInspectionDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(bikeInspectionService).delete(1L);

        mockMvc.perform(delete("/api/v1/bike-inspections/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
