package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.BikeTypeDTO;
import org.clickenrent.rentalservice.service.BikeTypeService;
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

@WebMvcTest(BikeTypeController.class)
@AutoConfigureMockMvc
class BikeTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeTypeService bikeTypeService;

    private BikeTypeDTO typeDTO;

    @BeforeEach
    void setUp() {
        typeDTO = BikeTypeDTO.builder()
                .id(1L)
                .name("Electric bike")
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllBikeTypes_ReturnsOk() throws Exception {
        when(bikeTypeService.getAllBikeTypes()).thenReturn(Arrays.asList(typeDTO));

        mockMvc.perform(get("/api/bike-types").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Electric bike"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBikeTypeById_ReturnsOk() throws Exception {
        when(bikeTypeService.getBikeTypeById(1L)).thenReturn(typeDTO);

        mockMvc.perform(get("/api/bike-types/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Electric bike"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBikeType_ReturnsCreated() throws Exception {
        when(bikeTypeService.createBikeType(any())).thenReturn(typeDTO);

        mockMvc.perform(post("/api/bike-types")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(typeDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBikeType_ReturnsOk() throws Exception {
        when(bikeTypeService.updateBikeType(eq(1L), any())).thenReturn(typeDTO);

        mockMvc.perform(put("/api/bike-types/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(typeDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBikeType_ReturnsNoContent() throws Exception {
        doNothing().when(bikeTypeService).deleteBikeType(1L);

        mockMvc.perform(delete("/api/bike-types/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}

