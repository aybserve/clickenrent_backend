package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.LocationDTO;
import org.clickenrent.rentalservice.service.LocationService;
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
 * Unit tests for LocationController.
 */
@WebMvcTest(LocationController.class)
@AutoConfigureMockMvc
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocationService locationService;

    private LocationDTO locationDTO;

    @BeforeEach
    void setUp() {
        locationDTO = LocationDTO.builder()
                .id(1L)
                .externalId("LOC001")
                .name("Amsterdam Central")
                .address("Stationsplein 1, Amsterdam")
                .companyId(1L)
                .isPublic(true)
                .description("Main location in Amsterdam")
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllLocations_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        Page<LocationDTO> page = new PageImpl<>(Collections.singletonList(locationDTO));
        when(locationService.getAllLocations(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/locations")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Amsterdam Central"));

        verify(locationService, times(1)).getAllLocations(any());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllLocations_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        Page<LocationDTO> page = new PageImpl<>(Collections.singletonList(locationDTO));
        when(locationService.getAllLocations(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/locations")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(locationService, times(1)).getAllLocations(any());
    }
    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllLocations_WithCustomerRole_ReturnsOk() throws Exception {
        // Given
        Page<LocationDTO> page = new PageImpl<>(Collections.singletonList(locationDTO));
        when(locationService.getAllLocations(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/locations")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(locationService, times(1)).getAllLocations(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLocationById_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(locationService.getLocationById(1L)).thenReturn(locationDTO);

        // When & Then
        mockMvc.perform(get("/api/locations/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Amsterdam Central"));

        verify(locationService, times(1)).getLocationById(1L);
    }
    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getLocationByExternalId_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        when(locationService.getLocationByExternalId("LOC001")).thenReturn(locationDTO);

        // When & Then
        mockMvc.perform(get("/api/locations/external/LOC001")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("LOC001"));

        verify(locationService, times(1)).getLocationByExternalId("LOC001");
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void getLocationByExternalId_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(locationService.getLocationByExternalId("LOC001")).thenReturn(locationDTO);

        // When & Then
        mockMvc.perform(get("/api/locations/external/LOC001")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(locationService, times(1)).getLocationByExternalId("LOC001");
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void createLocation_WithAdminRole_ReturnsCreated() throws Exception {
        // Given
        LocationDTO newLocation = LocationDTO.builder()
                .name("Rotterdam Center")
                .address("Central Station, Rotterdam")
                .companyId(1L)
                .isPublic(true)
                .build();

        LocationDTO createdLocation = LocationDTO.builder()
                .id(2L)
                .name("Rotterdam Center")
                .address("Central Station, Rotterdam")
                .companyId(1L)
                .isPublic(true)
                .build();

        when(locationService.createLocation(any(LocationDTO.class))).thenReturn(createdLocation);

        // When & Then
        mockMvc.perform(post("/api/locations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLocation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Rotterdam Center"));

        verify(locationService, times(1)).createLocation(any(LocationDTO.class));
    }
    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createLocation_WithSuperadminRole_ReturnsCreated() throws Exception {
        // Given
        when(locationService.createLocation(any(LocationDTO.class))).thenReturn(locationDTO);

        // When & Then
        mockMvc.perform(post("/api/locations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationDTO)))
                .andExpect(status().isCreated());

        verify(locationService, times(1)).createLocation(any(LocationDTO.class));
    }
    @Test
    @WithMockUser(roles = "B2B")
    void createLocation_WithB2BRole_ReturnsCreated() throws Exception {
        // Given
        when(locationService.createLocation(any(LocationDTO.class))).thenReturn(locationDTO);

        // When & Then
        mockMvc.perform(post("/api/locations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationDTO)))
                .andExpect(status().isCreated());

        verify(locationService, times(1)).createLocation(any(LocationDTO.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateLocation_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        LocationDTO updatedLocation = LocationDTO.builder()
                .id(1L)
                .externalId("LOC001")
                .name("Amsterdam Central Updated")
                .address("Stationsplein 1, Amsterdam")
                .companyId(1L)
                .isPublic(true)
                .build();

        when(locationService.updateLocation(eq(1L), any(LocationDTO.class))).thenReturn(updatedLocation);

        // When & Then
        mockMvc.perform(put("/api/locations/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Amsterdam Central Updated"));

        verify(locationService, times(1)).updateLocation(eq(1L), any(LocationDTO.class));
    }
    @Test
    @WithMockUser(roles = "B2B")
    void updateLocation_WithB2BRole_ReturnsOk() throws Exception {
        // Given
        when(locationService.updateLocation(eq(1L), any(LocationDTO.class))).thenReturn(locationDTO);

        // When & Then
        mockMvc.perform(put("/api/locations/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationDTO)))
                .andExpect(status().isOk());

        verify(locationService, times(1)).updateLocation(eq(1L), any(LocationDTO.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteLocation_WithAdminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(locationService).deleteLocation(1L);

        // When & Then
        mockMvc.perform(delete("/api/locations/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(locationService, times(1)).deleteLocation(1L);
    }
    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteLocation_WithSuperadminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(locationService).deleteLocation(1L);

        // When & Then
        mockMvc.perform(delete("/api/locations/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(locationService, times(1)).deleteLocation(1L);
}
}




