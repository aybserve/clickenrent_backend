package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.ChargingStationDTO;
import org.clickenrent.rentalservice.service.ChargingStationService;
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

@WebMvcTest(ChargingStationController.class)
@AutoConfigureMockMvc
class ChargingStationControllerTest extends BaseWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChargingStationService chargingStationService;

    private ChargingStationDTO stationDTO;

    @BeforeEach
    void setUp() {
        stationDTO = ChargingStationDTO.builder()
                .id(1L)
                .code("CS001")
                .chargingStationModelId(1L)
                .chargingStationStatusId(1L)
                .hubId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllChargingStations_ReturnsOk() throws Exception {
        Page<ChargingStationDTO> page = new PageImpl<>(Collections.singletonList(stationDTO));
        when(chargingStationService.getAllChargingStations(any())).thenReturn(page);

        mockMvc.perform(get("/api/charging-stations").with(csrf()))
                .andExpect(status().isOk());

        verify(chargingStationService, times(1)).getAllChargingStations(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getChargingStationById_ReturnsOk() throws Exception {
        when(chargingStationService.getChargingStationById(1L)).thenReturn(stationDTO);

        mockMvc.perform(get("/api/charging-stations/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("CS001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createChargingStation_ReturnsCreated() throws Exception {
        when(chargingStationService.createChargingStation(any())).thenReturn(stationDTO);

        mockMvc.perform(post("/api/charging-stations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stationDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateChargingStation_ReturnsOk() throws Exception {
        when(chargingStationService.updateChargingStation(eq(1L), any())).thenReturn(stationDTO);

        mockMvc.perform(put("/api/charging-stations/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stationDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteChargingStation_ReturnsNoContent() throws Exception {
        doNothing().when(chargingStationService).deleteChargingStation(1L);

        mockMvc.perform(delete("/api/charging-stations/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}








