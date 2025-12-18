package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.BikePartDTO;
import org.clickenrent.rentalservice.service.BikePartService;
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
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BikePartController.class)
@AutoConfigureMockMvc
class BikePartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikePartService bikePartService;

    private BikePartDTO bikePartDTO;

    @BeforeEach
    void setUp() {
        bikePartDTO = BikePartDTO.builder()
                .id(1L)
                .bikeId(1L)
                .partId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllBikeParts_ReturnsOk() throws Exception {
        Page<BikePartDTO> page = new PageImpl<>(Collections.singletonList(bikePartDTO));
        when(bikePartService.getAllBikeParts(any())).thenReturn(page);

        mockMvc.perform(get("/api/bike-parts").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBikePartById_ReturnsOk() throws Exception {
        when(bikePartService.getBikePartById(1L)).thenReturn(bikePartDTO);

        mockMvc.perform(get("/api/bike-parts/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bikeId").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBikePart_ReturnsCreated() throws Exception {
        when(bikePartService.createBikePart(any())).thenReturn(bikePartDTO);

        mockMvc.perform(post("/api/bike-parts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikePartDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBikePart_ReturnsNoContent() throws Exception {
        doNothing().when(bikePartService).deleteBikePart(1L);

        mockMvc.perform(delete("/api/bike-parts/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}

