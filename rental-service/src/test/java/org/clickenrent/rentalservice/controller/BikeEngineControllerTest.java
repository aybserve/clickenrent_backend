package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.BikeEngineDTO;
import org.clickenrent.rentalservice.service.BikeEngineService;
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

@WebMvcTest(BikeEngineController.class)
@AutoConfigureMockMvc
class BikeEngineControllerTest extends BaseWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeEngineService bikeEngineService;

    private BikeEngineDTO engineDTO;

    @BeforeEach
    void setUp() {
        engineDTO = BikeEngineDTO.builder()
                .id(1L)
                .externalId("BE001")
                .name("Bosch Performance Line 250W")
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllBikeEngines_ReturnsOk() throws Exception {
        Page<BikeEngineDTO> page = new PageImpl<>(Collections.singletonList(engineDTO));
        when(bikeEngineService.getAllBikeEngines(any())).thenReturn(page);

        mockMvc.perform(get("/api/bike-engines").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBikeEngineById_ReturnsOk() throws Exception {
        when(bikeEngineService.getBikeEngineById(1L)).thenReturn(engineDTO);

        mockMvc.perform(get("/api/bike-engines/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bosch Performance Line 250W"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBikeEngine_ReturnsCreated() throws Exception {
        when(bikeEngineService.createBikeEngine(any())).thenReturn(engineDTO);

        mockMvc.perform(post("/api/bike-engines")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(engineDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBikeEngine_ReturnsOk() throws Exception {
        when(bikeEngineService.updateBikeEngine(eq(1L), any())).thenReturn(engineDTO);

        mockMvc.perform(put("/api/bike-engines/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(engineDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBikeEngine_ReturnsNoContent() throws Exception {
        doNothing().when(bikeEngineService).deleteBikeEngine(1L);

        mockMvc.perform(delete("/api/bike-engines/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}








