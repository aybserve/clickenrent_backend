package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.BikeDTO;
import org.clickenrent.rentalservice.service.BikeService;
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
 * Unit tests for BikeController.
 */
@WebMvcTest(BikeController.class)
@AutoConfigureMockMvc
class BikeControllerTest extends BaseWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeService bikeService;

    private BikeDTO bikeDTO;

    @BeforeEach
    void setUp() {
        bikeDTO = BikeDTO.builder()
                .id(1L)
                .externalId("BIKE001")
                .code("BIKE001")
                .frameNumber("FR123456")
                .bikeStatusId(1L)
                .bikeTypeId(1L)
                .bikeModelId(1L)
                .hubId(1L)
                .isB2BRentable(false)
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllBikes_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        Page<BikeDTO> page = new PageImpl<>(Collections.singletonList(bikeDTO));
        when(bikeService.getAllBikes(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/bikes")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].code").value("BIKE001"))
                .andExpect(jsonPath("$.content[0].frameNumber").value("FR123456"));

        verify(bikeService, times(1)).getAllBikes(any());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllBikes_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        Page<BikeDTO> page = new PageImpl<>(Collections.singletonList(bikeDTO));
        when(bikeService.getAllBikes(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/bikes")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(bikeService, times(1)).getAllBikes(any());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getBikeById_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        when(bikeService.getBikeById(1L)).thenReturn(bikeDTO);

        // When & Then
        mockMvc.perform(get("/api/bikes/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.code").value("BIKE001"))
                .andExpect(jsonPath("$.frameNumber").value("FR123456"));

        verify(bikeService, times(1)).getBikeById(1L);
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void getBikeById_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(bikeService.getBikeById(1L)).thenReturn(bikeDTO);

        // When & Then
        mockMvc.perform(get("/api/bikes/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(bikeService, times(1)).getBikeById(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getBikeByCode_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        when(bikeService.getBikeByCode("BIKE001")).thenReturn(bikeDTO);

        // When & Then
        mockMvc.perform(get("/api/bikes/code/BIKE001")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("BIKE001"));

        verify(bikeService, times(1)).getBikeByCode("BIKE001");
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void createBike_WithValidRequest_ReturnsCreated() throws Exception {
        // Given
        BikeDTO newBike = BikeDTO.builder()
                .code("BIKE002")
                .frameNumber("FR654321")
                .bikeStatusId(1L)
                .bikeTypeId(1L)
                .bikeModelId(1L)
                .hubId(1L)
                .build();

        BikeDTO createdBike = BikeDTO.builder()
                .id(2L)
                .code("BIKE002")
                .frameNumber("FR654321")
                .bikeStatusId(1L)
                .bikeTypeId(1L)
                .bikeModelId(1L)
                .hubId(1L)
                .build();

        when(bikeService.createBike(any(BikeDTO.class))).thenReturn(createdBike);

        // When & Then
        mockMvc.perform(post("/api/bikes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBike)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.code").value("BIKE002"));

        verify(bikeService, times(1)).createBike(any(BikeDTO.class));
    }
    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createBike_WithSuperadminRole_ReturnsCreated() throws Exception {
        // Given
        when(bikeService.createBike(any(BikeDTO.class))).thenReturn(bikeDTO);

        // When & Then
        mockMvc.perform(post("/api/bikes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeDTO)))
                .andExpect(status().isCreated());

        verify(bikeService, times(1)).createBike(any(BikeDTO.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBike_WithValidRequest_ReturnsOk() throws Exception {
        // Given
        BikeDTO updatedBike = BikeDTO.builder()
                .id(1L)
                .code("BIKE001_UPDATED")
                .frameNumber("FR123456")
                .bikeStatusId(1L)
                .bikeTypeId(1L)
                .bikeModelId(1L)
                .hubId(1L)
                .build();

        when(bikeService.updateBike(eq(1L), any(BikeDTO.class))).thenReturn(updatedBike);

        // When & Then
        mockMvc.perform(put("/api/bikes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("BIKE001_UPDATED"));

        verify(bikeService, times(1)).updateBike(eq(1L), any(BikeDTO.class));
    }
    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateBike_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        when(bikeService.updateBike(eq(1L), any(BikeDTO.class))).thenReturn(bikeDTO);

        // When & Then
        mockMvc.perform(put("/api/bikes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeDTO)))
                .andExpect(status().isOk());

        verify(bikeService, times(1)).updateBike(eq(1L), any(BikeDTO.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBike_WithAdminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(bikeService).deleteBike(1L);

        // When & Then
        mockMvc.perform(delete("/api/bikes/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(bikeService, times(1)).deleteBike(1L);
    }
    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteBike_WithSuperadminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(bikeService).deleteBike(1L);

        // When & Then
        mockMvc.perform(delete("/api/bikes/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(bikeService, times(1)).deleteBike(1L);
}
}








