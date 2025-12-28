package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.ChargingStationModelDTO;
import org.clickenrent.rentalservice.service.ChargingStationModelService;
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

@WebMvcTest(ChargingStationModelController.class)
@AutoConfigureMockMvc
class ChargingStationModelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChargingStationModelService chargingStationModelService;

    private ChargingStationModelDTO modelDTO;

    @BeforeEach
    void setUp() {
        modelDTO = ChargingStationModelDTO.builder()
                .id(1L)
                .externalId("CSM001")
                .name("Wall Connector Gen 3")
                .chargingStationBrandId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllModels_ReturnsOk() throws Exception {
        Page<ChargingStationModelDTO> page = new PageImpl<>(Collections.singletonList(modelDTO));
        when(chargingStationModelService.getAllModels(any())).thenReturn(page);

        mockMvc.perform(get("/api/charging-station-models").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getModelById_ReturnsOk() throws Exception {
        when(chargingStationModelService.getModelById(1L)).thenReturn(modelDTO);

        mockMvc.perform(get("/api/charging-station-models/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Wall Connector Gen 3"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createModel_ReturnsCreated() throws Exception {
        when(chargingStationModelService.createModel(any())).thenReturn(modelDTO);

        mockMvc.perform(post("/api/charging-station-models")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modelDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateModel_ReturnsOk() throws Exception {
        when(chargingStationModelService.updateModel(eq(1L), any())).thenReturn(modelDTO);

        mockMvc.perform(put("/api/charging-station-models/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modelDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteModel_ReturnsNoContent() throws Exception {
        doNothing().when(chargingStationModelService).deleteModel(1L);

        mockMvc.perform(delete("/api/charging-station-models/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}








