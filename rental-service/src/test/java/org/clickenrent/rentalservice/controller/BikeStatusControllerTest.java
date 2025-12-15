package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.BikeStatusDTO;
import org.clickenrent.rentalservice.service.BikeStatusService;
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

@WebMvcTest(BikeStatusController.class)
@AutoConfigureMockMvc
class BikeStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeStatusService bikeStatusService;

    private BikeStatusDTO statusDTO;

    @BeforeEach
    void setUp() {
        statusDTO = BikeStatusDTO.builder()
                .id(1L)
                .name("Available")
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllBikeStatuses_ReturnsOk() throws Exception {
        when(bikeStatusService.getAllBikeStatuses()).thenReturn(Arrays.asList(statusDTO));

        mockMvc.perform(get("/api/bike-statuses").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Available"));
    }

    @Test
    void getAllBikeStatuses_WithoutAuth_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/bike-statuses").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBikeStatusById_ReturnsOk() throws Exception {
        when(bikeStatusService.getBikeStatusById(1L)).thenReturn(statusDTO);

        mockMvc.perform(get("/api/bike-statuses/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Available"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBikeStatus_ReturnsCreated() throws Exception {
        when(bikeStatusService.createBikeStatus(any())).thenReturn(statusDTO);

        mockMvc.perform(post("/api/bike-statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createBikeStatus_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/bike-statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBikeStatus_ReturnsOk() throws Exception {
        when(bikeStatusService.updateBikeStatus(eq(1L), any())).thenReturn(statusDTO);

        mockMvc.perform(put("/api/bike-statuses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBikeStatus_ReturnsNoContent() throws Exception {
        doNothing().when(bikeStatusService).deleteBikeStatus(1L);

        mockMvc.perform(delete("/api/bike-statuses/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
