package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.BikeModelPartDTO;
import org.clickenrent.rentalservice.service.BikeModelPartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for BikeModelPartController.
 */
@WebMvcTest(BikeModelPartController.class)
@AutoConfigureMockMvc
class BikeModelPartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeModelPartService bikeModelPartService;

    private BikeModelPartDTO bikeModelPartDTO;

    @BeforeEach
    void setUp() {
        bikeModelPartDTO = BikeModelPartDTO.builder()
                .id(1L)
                .externalId("BMP001")
                .bikeModelId(10L)
                .partId(20L)
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllBikeModelParts_WithSuperadminRole_ReturnsOk() throws Exception {
        Page<BikeModelPartDTO> page = new PageImpl<>(Collections.singletonList(bikeModelPartDTO));
        when(bikeModelPartService.getAllBikeModelParts(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/bike-model-parts").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].externalId").value("BMP001"));

        verify(bikeModelPartService, times(1)).getAllBikeModelParts(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllBikeModelParts_WithAdminRole_ReturnsOk() throws Exception {
        Page<BikeModelPartDTO> page = new PageImpl<>(Collections.singletonList(bikeModelPartDTO));
        when(bikeModelPartService.getAllBikeModelParts(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/bike-model-parts").with(csrf()))
                .andExpect(status().isOk());

        verify(bikeModelPartService, times(1)).getAllBikeModelParts(any());
    }

    @Test
    void getAllBikeModelParts_WithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/bike-model-parts").with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(bikeModelPartService, never()).getAllBikeModelParts(any());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllBikeModelParts_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/bike-model-parts").with(csrf()))
                .andExpect(status().isForbidden());

        verify(bikeModelPartService, never()).getAllBikeModelParts(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBikeModelPartById_WithAdminRole_ReturnsOk() throws Exception {
        when(bikeModelPartService.getBikeModelPartById(1L)).thenReturn(bikeModelPartDTO);

        mockMvc.perform(get("/api/v1/bike-model-parts/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bikeModelId").value(10))
                .andExpect(jsonPath("$.partId").value(20));

        verify(bikeModelPartService, times(1)).getBikeModelPartById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getBikeModelPartById_WithSuperadminRole_ReturnsOk() throws Exception {
        when(bikeModelPartService.getBikeModelPartById(1L)).thenReturn(bikeModelPartDTO);

        mockMvc.perform(get("/api/v1/bike-model-parts/1").with(csrf()))
                .andExpect(status().isOk());

        verify(bikeModelPartService, times(1)).getBikeModelPartById(1L);
    }

    @Test
    void getBikeModelPartById_WithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/bike-model-parts/1").with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(bikeModelPartService, never()).getBikeModelPartById(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBikeModelPart_WithAdminRole_ReturnsCreated() throws Exception {
        when(bikeModelPartService.createBikeModelPart(any())).thenReturn(bikeModelPartDTO);

        mockMvc.perform(post("/api/v1/bike-model-parts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeModelPartDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(bikeModelPartService, times(1)).createBikeModelPart(any(BikeModelPartDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createBikeModelPart_WithSuperadminRole_ReturnsCreated() throws Exception {
        when(bikeModelPartService.createBikeModelPart(any())).thenReturn(bikeModelPartDTO);

        mockMvc.perform(post("/api/v1/bike-model-parts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeModelPartDTO)))
                .andExpect(status().isCreated());

        verify(bikeModelPartService, times(1)).createBikeModelPart(any(BikeModelPartDTO.class));
    }

    @Test
    void createBikeModelPart_WithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/bike-model-parts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeModelPartDTO)))
                .andExpect(status().isUnauthorized());

        verify(bikeModelPartService, never()).createBikeModelPart(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBikeModelPart_WithAdminRole_ReturnsNoContent() throws Exception {
        doNothing().when(bikeModelPartService).deleteBikeModelPart(1L);

        mockMvc.perform(delete("/api/v1/bike-model-parts/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(bikeModelPartService, times(1)).deleteBikeModelPart(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteBikeModelPart_WithSuperadminRole_ReturnsNoContent() throws Exception {
        doNothing().when(bikeModelPartService).deleteBikeModelPart(1L);

        mockMvc.perform(delete("/api/v1/bike-model-parts/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(bikeModelPartService, times(1)).deleteBikeModelPart(1L);
    }

    @Test
    void deleteBikeModelPart_WithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/bike-model-parts/1").with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(bikeModelPartService, never()).deleteBikeModelPart(anyLong());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deleteBikeModelPart_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/bike-model-parts/1").with(csrf()))
                .andExpect(status().isForbidden());

        verify(bikeModelPartService, never()).deleteBikeModelPart(anyLong());
    }
}
