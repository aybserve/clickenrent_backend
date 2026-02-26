package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.BikeReservationDTO;
import org.clickenrent.rentalservice.service.BikeReservationService;
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
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for BikeReservationController.
 */
@WebMvcTest(BikeReservationController.class)
@AutoConfigureMockMvc
class BikeReservationControllerTest extends BaseWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeReservationService bikeReservationService;

    private BikeReservationDTO reservationDTO;

    @BeforeEach
    void setUp() {
        reservationDTO = BikeReservationDTO.builder()
                .id(1L)
                .externalId("BRES001")
                .userExternalId("usr-ext-00001")
                .bikeId(1L)
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllReservations_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        Page<BikeReservationDTO> page = new PageImpl<>(Collections.singletonList(reservationDTO));
        when(bikeReservationService.getAllReservations(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/bike-reservations")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].externalId").value("BRES001"));

        verify(bikeReservationService, times(1)).getAllReservations(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllReservations_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        Page<BikeReservationDTO> page = new PageImpl<>(Collections.singletonList(reservationDTO));
        when(bikeReservationService.getAllReservations(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/bike-reservations")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(bikeReservationService, times(1)).getAllReservations(any());
    }
    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getReservationsByUser_WithCustomerRole_ReturnsOk() throws Exception {
        // Given
        when(bikeReservationService.getReservationsByUserExternalId("usr-ext-00001")).thenReturn(Arrays.asList(reservationDTO));

        // When & Then
        mockMvc.perform(get("/api/bike-reservations/by-user/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(bikeReservationService, times(1)).getReservationsByUserExternalId("usr-ext-00001");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReservationsByUser_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(bikeReservationService.getReservationsByUserExternalId("usr-ext-00001")).thenReturn(Arrays.asList(reservationDTO));

        // When & Then
        mockMvc.perform(get("/api/bike-reservations/by-user/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(bikeReservationService, times(1)).getReservationsByUserExternalId("usr-ext-00001");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReservationById_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(bikeReservationService.getReservationById(1L)).thenReturn(reservationDTO);

        // When & Then
        mockMvc.perform(get("/api/bike-reservations/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.externalId").value("BRES001"));

        verify(bikeReservationService, times(1)).getReservationById(1L);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getReservationById_WithCustomerRole_ReturnsOk() throws Exception {
        // Given
        when(bikeReservationService.getReservationById(1L)).thenReturn(reservationDTO);

        // When & Then
        mockMvc.perform(get("/api/bike-reservations/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(bikeReservationService, times(1)).getReservationById(1L);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createReservation_WithCustomerRole_ReturnsCreated() throws Exception {
        // Given
        BikeReservationDTO newReservation = BikeReservationDTO.builder()
                .userExternalId("usr-ext-00001")
                .bikeId(2L)
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(2))
                .build();

        BikeReservationDTO createdReservation = BikeReservationDTO.builder()
                .id(2L)
                .externalId("BRES002")
                .userExternalId("usr-ext-00001")
                .bikeId(2L)
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(2))
                .build();

        when(bikeReservationService.createReservation(any(BikeReservationDTO.class))).thenReturn(createdReservation);

        // When & Then
        mockMvc.perform(post("/api/bike-reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReservation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));

        verify(bikeReservationService, times(1)).createReservation(any(BikeReservationDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createReservation_WithAdminRole_ReturnsCreated() throws Exception {
        // Given
        when(bikeReservationService.createReservation(any(BikeReservationDTO.class))).thenReturn(reservationDTO);

        // When & Then
        mockMvc.perform(post("/api/bike-reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationDTO)))
                .andExpect(status().isCreated());

        verify(bikeReservationService, times(1)).createReservation(any(BikeReservationDTO.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deleteReservation_WithCustomerRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(bikeReservationService).deleteReservation(1L);

        // When & Then
        mockMvc.perform(delete("/api/bike-reservations/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(bikeReservationService, times(1)).deleteReservation(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteReservation_WithAdminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(bikeReservationService).deleteReservation(1L);

        // When & Then
        mockMvc.perform(delete("/api/bike-reservations/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(bikeReservationService, times(1)).deleteReservation(1L);}
}




