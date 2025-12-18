package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.BikeModelRentalPlanDTO;
import org.clickenrent.rentalservice.service.BikeModelRentalPlanService;
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

@WebMvcTest(BikeModelRentalPlanController.class)
@AutoConfigureMockMvc
class BikeModelRentalPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeModelRentalPlanService bikeModelRentalPlanService;

    private BikeModelRentalPlanDTO dto;

    @BeforeEach
    void setUp() {
        dto = BikeModelRentalPlanDTO.builder()
                .id(1L)
                .bikeModelId(1L)
                .rentalPlanId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getPlansByBikeModel_ReturnsOk() throws Exception {
        List<BikeModelRentalPlanDTO> plans = Collections.singletonList(dto);
        when(bikeModelRentalPlanService.getPlansByBikeModel(1L)).thenReturn(plans);

        mockMvc.perform(get("/api/bike-model-rental-plans/by-bike-model/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bikeModelId").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(bikeModelRentalPlanService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/bike-model-rental-plans/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bikeModelId").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(bikeModelRentalPlanService.create(any())).thenReturn(dto);

        mockMvc.perform(post("/api/bike-model-rental-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(bikeModelRentalPlanService.update(eq(1L), any())).thenReturn(dto);

        mockMvc.perform(put("/api/bike-model-rental-plans/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(bikeModelRentalPlanService).delete(1L);

        mockMvc.perform(delete("/api/bike-model-rental-plans/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}

