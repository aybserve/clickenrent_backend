package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.RideDTO;
import org.clickenrent.rentalservice.service.RideService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RideController.class)
@AutoConfigureMockMvc
class RideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RideService rideService;

    private RideDTO rideDTO;

    @BeforeEach
    void setUp() {
        rideDTO = RideDTO.builder()
                .id(1L)
                .externalId("RIDE001")
                .bikeRentalId(1L)
                .startDateTime(LocalDateTime.now())
                .endDateTime(null)
                .startLocationId(1L)
                .endLocationId(2L)
                .coordinatesId(1L)
                .rideStatusId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRides_ReturnsOk() throws Exception {
        Page<RideDTO> page = new PageImpl<>(Collections.singletonList(rideDTO));
        when(rideService.getAllRides(any())).thenReturn(page);

        mockMvc.perform(get("/api/rides").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getRidesByBikeRental_ReturnsOk() throws Exception {
        List<RideDTO> rides = Arrays.asList(rideDTO);
        when(rideService.getRidesByBikeRental(1L)).thenReturn(rides);

        mockMvc.perform(get("/api/rides/by-bike-rental/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].externalId").value("RIDE001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRideById_ReturnsOk() throws Exception {
        when(rideService.getRideById(1L)).thenReturn(rideDTO);

        mockMvc.perform(get("/api/rides/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("RIDE001"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void startRide_ReturnsCreated() throws Exception {
        when(rideService.startRide(any())).thenReturn(rideDTO);

        mockMvc.perform(post("/api/rides/start")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void endRide_ReturnsOk() throws Exception {
        when(rideService.endRide(eq(1L), any())).thenReturn(rideDTO);

        mockMvc.perform(put("/api/rides/1/end")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRide_ReturnsNoContent() throws Exception {
        doNothing().when(rideService).deleteRide(1L);

        mockMvc.perform(delete("/api/rides/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}


