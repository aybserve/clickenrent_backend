package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.BikeRentalStatusDTO;
import org.clickenrent.rentalservice.service.BikeRentalStatusService;
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

@WebMvcTest(BikeRentalStatusController.class)
@AutoConfigureMockMvc
class BikeRentalStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeRentalStatusService bikeRentalStatusService;

    private BikeRentalStatusDTO statusDTO;

    @BeforeEach
    void setUp() {
        statusDTO = BikeRentalStatusDTO.builder()
                .id(1L)
                .name("Active")
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllStatuses_ReturnsOk() throws Exception {
        when(bikeRentalStatusService.getAllStatuses()).thenReturn(Arrays.asList(statusDTO));

        mockMvc.perform(get("/api/bike-rental-statuses").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Active"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStatusById_ReturnsOk() throws Exception {
        when(bikeRentalStatusService.getStatusById(1L)).thenReturn(statusDTO);

        mockMvc.perform(get("/api/bike-rental-statuses/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Active"));
    }
}
