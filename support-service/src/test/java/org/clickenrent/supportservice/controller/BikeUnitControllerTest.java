package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.BikeUnitDTO;
import org.clickenrent.supportservice.service.BikeUnitService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BikeUnitController.class)
@AutoConfigureMockMvc
class BikeUnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeUnitService bikeUnitService;

    private BikeUnitDTO bikeUnitDTO;

    @BeforeEach
    void setUp() {
        bikeUnitDTO = BikeUnitDTO.builder()
                .id(1L)
                .externalId("unit-uuid-1")
                .name("Unit A")
                .companyExternalId("company-uuid-1")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(bikeUnitService.getAll()).thenReturn(Arrays.asList(bikeUnitDTO));

        mockMvc.perform(get("/api/v1/bike-units").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Unit A"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(bikeUnitService.getById(1L)).thenReturn(bikeUnitDTO);

        mockMvc.perform(get("/api/v1/bike-units/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Unit A"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByExternalId_ReturnsOk() throws Exception {
        when(bikeUnitService.getByExternalId("unit-uuid-1")).thenReturn(bikeUnitDTO);

        mockMvc.perform(get("/api/v1/bike-units/external/unit-uuid-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("unit-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByCompanyExternalId_ReturnsOk() throws Exception {
        when(bikeUnitService.getByCompanyExternalId("company-uuid-1")).thenReturn(Arrays.asList(bikeUnitDTO));

        mockMvc.perform(get("/api/v1/bike-units/company/company-uuid-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyExternalId").value("company-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(bikeUnitService.create(any())).thenReturn(bikeUnitDTO);

        mockMvc.perform(post("/api/v1/bike-units")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeUnitDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(bikeUnitService.update(eq(1L), any())).thenReturn(bikeUnitDTO);

        mockMvc.perform(put("/api/v1/bike-units/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeUnitDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(bikeUnitService).delete(1L);

        mockMvc.perform(delete("/api/v1/bike-units/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
