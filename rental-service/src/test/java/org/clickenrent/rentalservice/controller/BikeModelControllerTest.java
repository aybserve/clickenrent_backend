package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.BikeModelDTO;
import org.clickenrent.rentalservice.service.BikeModelService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for BikeModelController.
 */
@WebMvcTest(BikeModelController.class)
@AutoConfigureMockMvc
class BikeModelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeModelService bikeModelService;

    private BikeModelDTO bikeModelDTO;

    @BeforeEach
    void setUp() {
        bikeModelDTO = BikeModelDTO.builder()
                .id(1L)
                .externalId("BM001")
                .name("VanMoof S3")
                .bikeBrandId(1L)
                .bikeTypeId(1L)
                .bikeEngineId(1L)
                .imageUrl("https://example.com/vanmoof-s3.jpg")
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllBikeModels_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        Page<BikeModelDTO> page = new PageImpl<>(Collections.singletonList(bikeModelDTO));
        when(bikeModelService.getAllBikeModels(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/bike-models")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("VanMoof S3"));

        verify(bikeModelService, times(1)).getAllBikeModels(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllBikeModels_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        Page<BikeModelDTO> page = new PageImpl<>(Collections.singletonList(bikeModelDTO));
        when(bikeModelService.getAllBikeModels(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/bike-models")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(bikeModelService, times(1)).getAllBikeModels(any());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllBikeModels_WithCustomerRole_ReturnsOk() throws Exception {
        // Given
        Page<BikeModelDTO> page = new PageImpl<>(Collections.singletonList(bikeModelDTO));
        when(bikeModelService.getAllBikeModels(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/bike-models")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(bikeModelService, times(1)).getAllBikeModels(any());
    }

    @Test
    void getAllBikeModels_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bike-models")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(bikeModelService, never()).getAllBikeModels(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBikeModelById_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(bikeModelService.getBikeModelById(1L)).thenReturn(bikeModelDTO);

        // When & Then
        mockMvc.perform(get("/api/bike-models/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("VanMoof S3"));

        verify(bikeModelService, times(1)).getBikeModelById(1L);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getBikeModelById_WithCustomerRole_ReturnsOk() throws Exception {
        // Given
        when(bikeModelService.getBikeModelById(1L)).thenReturn(bikeModelDTO);

        // When & Then
        mockMvc.perform(get("/api/bike-models/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(bikeModelService, times(1)).getBikeModelById(1L);
    }

    @Test
    void getBikeModelById_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bike-models/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(bikeModelService, never()).getBikeModelById(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBikeModel_WithAdminRole_ReturnsCreated() throws Exception {
        // Given
        BikeModelDTO newModel = BikeModelDTO.builder()
                .name("Gazelle Ultimate")
                .bikeBrandId(2L)
                .bikeTypeId(1L)
                .bikeEngineId(2L)
                .build();

        BikeModelDTO createdModel = BikeModelDTO.builder()
                .id(2L)
                .name("Gazelle Ultimate")
                .bikeBrandId(2L)
                .bikeTypeId(1L)
                .bikeEngineId(2L)
                .build();

        when(bikeModelService.createBikeModel(any(BikeModelDTO.class))).thenReturn(createdModel);

        // When & Then
        mockMvc.perform(post("/api/bike-models")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newModel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Gazelle Ultimate"));

        verify(bikeModelService, times(1)).createBikeModel(any(BikeModelDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createBikeModel_WithSuperadminRole_ReturnsCreated() throws Exception {
        // Given
        when(bikeModelService.createBikeModel(any(BikeModelDTO.class))).thenReturn(bikeModelDTO);

        // When & Then
        mockMvc.perform(post("/api/bike-models")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeModelDTO)))
                .andExpect(status().isCreated());

        verify(bikeModelService, times(1)).createBikeModel(any(BikeModelDTO.class));
    }

    @Test
    @WithMockUser(roles = "B2B")
    void createBikeModel_WithB2BRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/bike-models")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeModelDTO)))
                .andExpect(status().isForbidden());

        verify(bikeModelService, never()).createBikeModel(any(BikeModelDTO.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createBikeModel_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/bike-models")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeModelDTO)))
                .andExpect(status().isForbidden());

        verify(bikeModelService, never()).createBikeModel(any(BikeModelDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBikeModel_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        BikeModelDTO updatedModel = BikeModelDTO.builder()
                .id(1L)
                .externalId("BM001")
                .name("VanMoof S3 Updated")
                .bikeBrandId(1L)
                .bikeTypeId(1L)
                .bikeEngineId(1L)
                .build();

        when(bikeModelService.updateBikeModel(eq(1L), any(BikeModelDTO.class))).thenReturn(updatedModel);

        // When & Then
        mockMvc.perform(put("/api/bike-models/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeModelDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("VanMoof S3 Updated"));

        verify(bikeModelService, times(1)).updateBikeModel(eq(1L), any(BikeModelDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateBikeModel_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        when(bikeModelService.updateBikeModel(eq(1L), any(BikeModelDTO.class))).thenReturn(bikeModelDTO);

        // When & Then
        mockMvc.perform(put("/api/bike-models/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeModelDTO)))
                .andExpect(status().isOk());

        verify(bikeModelService, times(1)).updateBikeModel(eq(1L), any(BikeModelDTO.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateBikeModel_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/bike-models/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeModelDTO)))
                .andExpect(status().isForbidden());

        verify(bikeModelService, never()).updateBikeModel(anyLong(), any(BikeModelDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBikeModel_WithAdminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(bikeModelService).deleteBikeModel(1L);

        // When & Then
        mockMvc.perform(delete("/api/bike-models/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(bikeModelService, times(1)).deleteBikeModel(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteBikeModel_WithSuperadminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(bikeModelService).deleteBikeModel(1L);

        // When & Then
        mockMvc.perform(delete("/api/bike-models/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(bikeModelService, times(1)).deleteBikeModel(1L);
    }

    @Test
    @WithMockUser(roles = "B2B")
    void deleteBikeModel_WithB2BRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/bike-models/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(bikeModelService, never()).deleteBikeModel(anyLong());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deleteBikeModel_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/bike-models/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(bikeModelService, never()).deleteBikeModel(anyLong());
    }
}
