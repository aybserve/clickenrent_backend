package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.BikeInspectionItemBikeIssueDTO;
import org.clickenrent.supportservice.service.BikeInspectionItemBikeIssueService;
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

@WebMvcTest(BikeInspectionItemBikeIssueController.class)
@AutoConfigureMockMvc
class BikeInspectionItemBikeIssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeInspectionItemBikeIssueService bikeInspectionItemBikeIssueService;

    private BikeInspectionItemBikeIssueDTO linkDTO;

    @BeforeEach
    void setUp() {
        linkDTO = BikeInspectionItemBikeIssueDTO.builder()
                .id(1L)
                .externalId("link-uuid-1")
                .bikeInspectionItemId(1L)
                .bikeIssueId(1L)
                .bikeIssueName("Battery Issue")
                .companyExternalId("company-uuid-1")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(bikeInspectionItemBikeIssueService.getAll()).thenReturn(Arrays.asList(linkDTO));

        mockMvc.perform(get("/api/v1/bike-inspection-item-bike-issues").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bikeInspectionItemId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(bikeInspectionItemBikeIssueService.getById(1L)).thenReturn(linkDTO);

        mockMvc.perform(get("/api/v1/bike-inspection-item-bike-issues/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("link-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByExternalId_ReturnsOk() throws Exception {
        when(bikeInspectionItemBikeIssueService.getByExternalId("link-uuid-1")).thenReturn(linkDTO);

        mockMvc.perform(get("/api/v1/bike-inspection-item-bike-issues/external/link-uuid-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("link-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByBikeInspectionItemId_ReturnsOk() throws Exception {
        when(bikeInspectionItemBikeIssueService.getByBikeInspectionItemId(1L)).thenReturn(Arrays.asList(linkDTO));

        mockMvc.perform(get("/api/v1/bike-inspection-item-bike-issues/inspection-item/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bikeInspectionItemId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByBikeIssueId_ReturnsOk() throws Exception {
        when(bikeInspectionItemBikeIssueService.getByBikeIssueId(1L)).thenReturn(Arrays.asList(linkDTO));

        mockMvc.perform(get("/api/v1/bike-inspection-item-bike-issues/bike-issue/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bikeIssueId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByCompanyExternalId_ReturnsOk() throws Exception {
        when(bikeInspectionItemBikeIssueService.getByCompanyExternalId("company-uuid-1")).thenReturn(Arrays.asList(linkDTO));

        mockMvc.perform(get("/api/v1/bike-inspection-item-bike-issues/company/company-uuid-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyExternalId").value("company-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(bikeInspectionItemBikeIssueService.create(any())).thenReturn(linkDTO);

        mockMvc.perform(post("/api/v1/bike-inspection-item-bike-issues")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(bikeInspectionItemBikeIssueService.update(eq(1L), any())).thenReturn(linkDTO);

        mockMvc.perform(put("/api/v1/bike-inspection-item-bike-issues/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(bikeInspectionItemBikeIssueService).delete(1L);

        mockMvc.perform(delete("/api/v1/bike-inspection-item-bike-issues/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
