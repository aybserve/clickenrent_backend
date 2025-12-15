package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.BikeRentalDTO;
import org.clickenrent.rentalservice.service.BikeRentalService;
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

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for BikeRentalController.
 */
@WebMvcTest(BikeRentalController.class)
@AutoConfigureMockMvc
class BikeRentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeRentalService bikeRentalService;

    private BikeRentalDTO bikeRentalDTO;

    @BeforeEach
    void setUp() {
        bikeRentalDTO = BikeRentalDTO.builder()
                .id(1L)
                .externalId("BR001")
                .rentalId(1L)
                .bikeId(1L)
                .locationId(1L)
                .bikeRentalStatusId(1L)
                .rentalUnitId(1L)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllBikeRentals_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        Page<BikeRentalDTO> page = new PageImpl<>(Collections.singletonList(bikeRentalDTO));
        when(bikeRentalService.getAllBikeRentals(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/bike-rentals")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].externalId").value("BR001"));

        verify(bikeRentalService, times(1)).getAllBikeRentals(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllBikeRentals_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        Page<BikeRentalDTO> page = new PageImpl<>(Collections.singletonList(bikeRentalDTO));
        when(bikeRentalService.getAllBikeRentals(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/bike-rentals")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(bikeRentalService, times(1)).getAllBikeRentals(any());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllBikeRentals_WithCustomerRole_ReturnsOk() throws Exception {
        // Given - Customer sees their rentals
        Page<BikeRentalDTO> page = new PageImpl<>(Collections.singletonList(bikeRentalDTO));
        when(bikeRentalService.getAllBikeRentals(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/bike-rentals")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(bikeRentalService, times(1)).getAllBikeRentals(any());
    }

    @Test
    void getAllBikeRentals_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bike-rentals")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(bikeRentalService, never()).getAllBikeRentals(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBikeRentalById_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(bikeRentalService.getBikeRentalById(1L)).thenReturn(bikeRentalDTO);

        // When & Then
        mockMvc.perform(get("/api/bike-rentals/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.externalId").value("BR001"));

        verify(bikeRentalService, times(1)).getBikeRentalById(1L);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getBikeRentalById_WithCustomerRole_ReturnsOk() throws Exception {
        // Given
        when(bikeRentalService.getBikeRentalById(1L)).thenReturn(bikeRentalDTO);

        // When & Then
        mockMvc.perform(get("/api/bike-rentals/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(bikeRentalService, times(1)).getBikeRentalById(1L);
    }

    @Test
    void getBikeRentalById_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bike-rentals/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(bikeRentalService, never()).getBikeRentalById(anyLong());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createBikeRental_WithCustomerRole_ReturnsCreated() throws Exception {
        // Given
        BikeRentalDTO newRental = BikeRentalDTO.builder()
                .rentalId(1L)
                .bikeId(2L)
                .locationId(1L)
                .bikeRentalStatusId(1L)
                .rentalUnitId(1L)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .build();

        BikeRentalDTO createdRental = BikeRentalDTO.builder()
                .id(2L)
                .externalId("BR002")
                .rentalId(1L)
                .bikeId(2L)
                .locationId(1L)
                .bikeRentalStatusId(1L)
                .rentalUnitId(1L)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .build();

        when(bikeRentalService.createBikeRental(any(BikeRentalDTO.class))).thenReturn(createdRental);

        // When & Then
        mockMvc.perform(post("/api/bike-rentals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRental)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));

        verify(bikeRentalService, times(1)).createBikeRental(any(BikeRentalDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBikeRental_WithAdminRole_ReturnsCreated() throws Exception {
        // Given
        when(bikeRentalService.createBikeRental(any(BikeRentalDTO.class))).thenReturn(bikeRentalDTO);

        // When & Then
        mockMvc.perform(post("/api/bike-rentals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeRentalDTO)))
                .andExpect(status().isCreated());

        verify(bikeRentalService, times(1)).createBikeRental(any(BikeRentalDTO.class));
    }

    @Test
    void createBikeRental_WithoutAuthentication_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/bike-rentals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeRentalDTO)))
                .andExpect(status().isForbidden());

        verify(bikeRentalService, never()).createBikeRental(any(BikeRentalDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBikeRental_WithAdminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(bikeRentalService).deleteBikeRental(1L);

        // When & Then
        mockMvc.perform(delete("/api/bike-rentals/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(bikeRentalService, times(1)).deleteBikeRental(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteBikeRental_WithSuperadminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(bikeRentalService).deleteBikeRental(1L);

        // When & Then
        mockMvc.perform(delete("/api/bike-rentals/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(bikeRentalService, times(1)).deleteBikeRental(1L);
    }

    @Test
    @WithMockUser(roles = "B2B")
    void deleteBikeRental_WithB2BRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/bike-rentals/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(bikeRentalService, never()).deleteBikeRental(anyLong());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deleteBikeRental_WithCustomerRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/bike-rentals/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(bikeRentalService, never()).deleteBikeRental(anyLong());
    }
}
