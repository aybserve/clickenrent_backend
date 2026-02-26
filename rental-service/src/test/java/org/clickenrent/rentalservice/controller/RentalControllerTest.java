package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.RentalDTO;
import org.clickenrent.rentalservice.service.RentalService;
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
 * Unit tests for RentalController.
 */
@WebMvcTest(RentalController.class)
@AutoConfigureMockMvc
class RentalControllerTest extends BaseWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RentalService rentalService;

    private RentalDTO rentalDTO;

    @BeforeEach
    void setUp() {
        rentalDTO = RentalDTO.builder()
                .id(1L)
                .externalId("RENT001")
                .userExternalId("usr-ext-00001")
                .companyExternalId("company-ext-001")
                .rentalStatusId(2L)
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllRentals_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        Page<RentalDTO> page = new PageImpl<>(Collections.singletonList(rentalDTO));
        when(rentalService.getAllRentals(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/rentals")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].externalId").value("RENT001"));

        verify(rentalService, times(1)).getAllRentals(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRentals_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        Page<RentalDTO> page = new PageImpl<>(Collections.singletonList(rentalDTO));
        when(rentalService.getAllRentals(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/rentals")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(rentalService, times(1)).getAllRentals(any());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllRentals_WithCustomerRole_ReturnsOk() throws Exception {
        // Given - Customer sees only their rentals
        Page<RentalDTO> page = new PageImpl<>(Collections.singletonList(rentalDTO));
        when(rentalService.getAllRentals(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/rentals")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(rentalService, times(1)).getAllRentals(any());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getRentalById_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        when(rentalService.getRentalById(1L)).thenReturn(rentalDTO);

        // When & Then
        mockMvc.perform(get("/api/rentals/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.externalId").value("RENT001"));

        verify(rentalService, times(1)).getRentalById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRentalById_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(rentalService.getRentalById(1L)).thenReturn(rentalDTO);

        // When & Then
        mockMvc.perform(get("/api/rentals/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(rentalService, times(1)).getRentalById(1L);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createRental_WithCustomerRole_ReturnsCreated() throws Exception {
        // Given
        RentalDTO newRental = RentalDTO.builder()
                .userExternalId("usr-ext-00001")
                .companyExternalId("company-ext-001")
                .rentalStatusId(1L)
                .build();

        RentalDTO createdRental = RentalDTO.builder()
                .id(2L)
                .userExternalId("usr-ext-00001")
                .companyExternalId("company-ext-001")
                .rentalStatusId(1L)
                .build();

        when(rentalService.createRental(any(RentalDTO.class))).thenReturn(createdRental);

        // When & Then
        mockMvc.perform(post("/api/rentals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRental)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));

        verify(rentalService, times(1)).createRental(any(RentalDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRental_WithAdminRole_ReturnsCreated() throws Exception {
        // Given
        when(rentalService.createRental(any(RentalDTO.class))).thenReturn(rentalDTO);

        // When & Then
        mockMvc.perform(post("/api/rentals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalDTO)))
                .andExpect(status().isCreated());

        verify(rentalService, times(1)).createRental(any(RentalDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRental_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        RentalDTO updatedRental = RentalDTO.builder()
                .id(1L)
                .externalId("RENT001")
                .userExternalId("usr-ext-00001")
                .companyExternalId("company-ext-001")
                .rentalStatusId(3L)
                .build();

        when(rentalService.updateRental(eq(1L), any(RentalDTO.class))).thenReturn(updatedRental);

        // When & Then
        mockMvc.perform(put("/api/rentals/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalStatusId").value(3L));

        verify(rentalService, times(1)).updateRental(eq(1L), any(RentalDTO.class));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateRental_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        when(rentalService.updateRental(eq(1L), any(RentalDTO.class))).thenReturn(rentalDTO);

        // When & Then
        mockMvc.perform(put("/api/rentals/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalDTO)))
                .andExpect(status().isOk());

        verify(rentalService, times(1)).updateRental(eq(1L), any(RentalDTO.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateRental_WithCustomerRole_ReturnsOk() throws Exception {
        // Given - Customers can update their own rentals
        when(rentalService.updateRental(eq(1L), any(RentalDTO.class))).thenReturn(rentalDTO);

        // When & Then
        mockMvc.perform(put("/api/rentals/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalDTO)))
                .andExpect(status().isOk());

        verify(rentalService, times(1)).updateRental(eq(1L), any(RentalDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRental_WithAdminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(rentalService).deleteRental(1L);

        // When & Then
        mockMvc.perform(delete("/api/rentals/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(rentalService, times(1)).deleteRental(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteRental_WithSuperadminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(rentalService).deleteRental(1L);

        // When & Then
        mockMvc.perform(delete("/api/rentals/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(rentalService, times(1)).deleteRental(1L);
    }
}
