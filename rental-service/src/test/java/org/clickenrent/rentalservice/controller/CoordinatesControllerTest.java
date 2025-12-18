package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.CoordinatesDTO;
import org.clickenrent.rentalservice.service.CoordinatesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CoordinatesController.class)
@AutoConfigureMockMvc
class CoordinatesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CoordinatesService coordinatesService;

    private CoordinatesDTO coordinatesDTO;

    @BeforeEach
    void setUp() {
        coordinatesDTO = CoordinatesDTO.builder()
                .id(1L)
                .latitude(new BigDecimal("52.370216"))
                .longitude(new BigDecimal("4.895168"))
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCoordinatesById_ReturnsOk() throws Exception {
        when(coordinatesService.getCoordinatesById(1L)).thenReturn(coordinatesDTO);

        mockMvc.perform(get("/api/coordinates/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(52.370216));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCoordinates_ReturnsCreated() throws Exception {
        when(coordinatesService.createCoordinates(any())).thenReturn(coordinatesDTO);

        mockMvc.perform(post("/api/coordinates")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coordinatesDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCoordinates_ReturnsOk() throws Exception {
        when(coordinatesService.updateCoordinates(eq(1L), any())).thenReturn(coordinatesDTO);

        mockMvc.perform(put("/api/coordinates/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coordinatesDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCoordinates_ReturnsNoContent() throws Exception {
        doNothing().when(coordinatesService).deleteCoordinates(1L);

        mockMvc.perform(delete("/api/coordinates/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}

