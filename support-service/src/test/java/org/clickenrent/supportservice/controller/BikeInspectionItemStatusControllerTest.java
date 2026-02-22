package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.BikeInspectionItemStatusDTO;
import org.clickenrent.supportservice.service.BikeInspectionItemStatusService;
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

@WebMvcTest(BikeInspectionItemStatusController.class)
@AutoConfigureMockMvc
class BikeInspectionItemStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeInspectionItemStatusService bikeInspectionItemStatusService;

    private BikeInspectionItemStatusDTO statusDTO;

    @BeforeEach
    void setUp() {
        statusDTO = BikeInspectionItemStatusDTO.builder()
                .id(1L)
                .externalId("item-status-uuid-1")
                .name("OK")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(bikeInspectionItemStatusService.getAll()).thenReturn(Arrays.asList(statusDTO));

        mockMvc.perform(get("/api/v1/bike-inspection-item-statuses").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("OK"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(bikeInspectionItemStatusService.getById(1L)).thenReturn(statusDTO);

        mockMvc.perform(get("/api/v1/bike-inspection-item-statuses/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("OK"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByExternalId_ReturnsOk() throws Exception {
        when(bikeInspectionItemStatusService.getByExternalId("item-status-uuid-1")).thenReturn(statusDTO);

        mockMvc.perform(get("/api/v1/bike-inspection-item-statuses/external/item-status-uuid-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("item-status-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByName_ReturnsOk() throws Exception {
        when(bikeInspectionItemStatusService.getByName("OK")).thenReturn(statusDTO);

        mockMvc.perform(get("/api/v1/bike-inspection-item-statuses/name/OK").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("OK"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(bikeInspectionItemStatusService.create(any())).thenReturn(statusDTO);

        mockMvc.perform(post("/api/v1/bike-inspection-item-statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(bikeInspectionItemStatusService.update(eq(1L), any())).thenReturn(statusDTO);

        mockMvc.perform(put("/api/v1/bike-inspection-item-statuses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(bikeInspectionItemStatusService).delete(1L);

        mockMvc.perform(delete("/api/v1/bike-inspection-item-statuses/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
