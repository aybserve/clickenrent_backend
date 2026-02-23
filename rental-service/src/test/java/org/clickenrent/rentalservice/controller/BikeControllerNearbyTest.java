package org.clickenrent.rentalservice.controller;

import org.clickenrent.rentalservice.dto.BikeLocationDTO;
import org.clickenrent.rentalservice.dto.GeoPointDTO;
import org.clickenrent.rentalservice.dto.NearbyBikesResponseDTO;
import org.clickenrent.rentalservice.service.BikeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for BikeController nearby endpoint.
 */
@WebMvcTest(BikeController.class)
@AutoConfigureMockMvc
class BikeControllerNearbyTest extends BaseWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BikeService bikeService;

    @Test
    @WithMockUser(roles = "USER")
    void testGetNearbyBikes_Success() throws Exception {
        // Arrange
        BikeLocationDTO bike1 = BikeLocationDTO.builder()
                .id("bike-uuid-1")
                .bikeCode("EB001")
                .bikeModelName("Zigma E-bike")
                .bikeStatus(1L)
                .bikeStatusName("Available")
                .batteryLevel(75)
                .coordinates(GeoPointDTO.builder()
                        .latitude(new BigDecimal("52.374"))
                        .longitude(new BigDecimal("4.901"))
                        .build())
                .distance(0.3)
                .distanceUnit("km")
                .hubExternalId("hub-uuid-1")
                .hubName("Hub A - Main")
                .build();

        BikeLocationDTO bike2 = BikeLocationDTO.builder()
                .id("bike-uuid-2")
                .bikeCode("EB002")
                .bikeModelName("City Bike")
                .bikeStatus(1L)
                .bikeStatusName("Available")
                .batteryLevel(100)
                .coordinates(GeoPointDTO.builder()
                        .latitude(new BigDecimal("52.375"))
                        .longitude(new BigDecimal("4.902"))
                        .build())
                .distance(0.5)
                .distanceUnit("km")
                .hubExternalId("hub-uuid-1")
                .hubName("Hub A - Main")
                .build();

        List<BikeLocationDTO> bikes = Arrays.asList(bike1, bike2);

        NearbyBikesResponseDTO response = NearbyBikesResponseDTO.builder()
                .bikes(bikes)
                .total(12L)
                .build();

        when(bikeService.findNearbyBikes(anyDouble(), anyDouble(), anyDouble(), anyInt(), any()))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/bikes/nearby")
                        .param("lat", "52.374")
                        .param("lng", "4.9")
                        .param("radius", "5")
                        .param("limit", "50")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bikes").isArray())
                .andExpect(jsonPath("$.bikes[0].id").value("bike-uuid-1"))
                .andExpect(jsonPath("$.bikes[0].bikeCode").value("EB001"))
                .andExpect(jsonPath("$.bikes[0].distance").value(0.3))
                .andExpect(jsonPath("$.bikes[0].distanceUnit").value("km"))
                .andExpect(jsonPath("$.total").value(12));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetNearbyBikes_WithStatusFilter() throws Exception {
        // Arrange
        NearbyBikesResponseDTO response = NearbyBikesResponseDTO.builder()
                .bikes(Arrays.asList())
                .total(0L)
                .build();

        when(bikeService.findNearbyBikes(anyDouble(), anyDouble(), anyDouble(), anyInt(), eq(1L)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/bikes/nearby")
                        .param("lat", "52.374")
                        .param("lng", "4.9")
                        .param("radius", "5")
                        .param("status", "1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bikes").isArray())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void testGetNearbyBikes_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/bikes/nearby")
                        .param("lat", "52.374")
                        .param("lng", "4.9")
                        .param("radius", "5")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetNearbyBikes_InvalidCoordinates() throws Exception {
        // Arrange
        when(bikeService.findNearbyBikes(anyDouble(), anyDouble(), anyDouble(), anyInt(), any()))
                .thenThrow(new IllegalArgumentException("Invalid coordinates"));

        // Act & Assert
        mockMvc.perform(get("/api/bikes/nearby")
                        .param("lat", "200")
                        .param("lng", "4.9")
                        .param("radius", "5")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

