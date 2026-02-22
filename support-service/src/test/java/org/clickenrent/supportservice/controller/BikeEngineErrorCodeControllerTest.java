package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.BikeEngineErrorCodeDTO;
import org.clickenrent.supportservice.service.BikeEngineErrorCodeService;
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

@WebMvcTest(BikeEngineErrorCodeController.class)
@AutoConfigureMockMvc
class BikeEngineErrorCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeEngineErrorCodeService bikeEngineErrorCodeService;

    private BikeEngineErrorCodeDTO linkDTO;

    @BeforeEach
    void setUp() {
        linkDTO = BikeEngineErrorCodeDTO.builder()
                .id(1L)
                .externalId("link-uuid-1")
                .bikeEngineExternalId("engine-uuid-1")
                .errorCodeId(1L)
                .errorCodeName("E001")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(bikeEngineErrorCodeService.getAll()).thenReturn(Arrays.asList(linkDTO));

        mockMvc.perform(get("/api/v1/bike-engine-error-codes").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bikeEngineExternalId").value("engine-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(bikeEngineErrorCodeService.getById(1L)).thenReturn(linkDTO);

        mockMvc.perform(get("/api/v1/bike-engine-error-codes/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("link-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByExternalId_ReturnsOk() throws Exception {
        when(bikeEngineErrorCodeService.getByExternalId("link-uuid-1")).thenReturn(linkDTO);

        mockMvc.perform(get("/api/v1/bike-engine-error-codes/external/link-uuid-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("link-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByBikeEngineExternalId_ReturnsOk() throws Exception {
        when(bikeEngineErrorCodeService.getByBikeEngineExternalId("engine-uuid-1")).thenReturn(Arrays.asList(linkDTO));

        mockMvc.perform(get("/api/v1/bike-engine-error-codes/bike-engine/engine-uuid-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bikeEngineExternalId").value("engine-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByErrorCodeId_ReturnsOk() throws Exception {
        when(bikeEngineErrorCodeService.getByErrorCodeId(1L)).thenReturn(Arrays.asList(linkDTO));

        mockMvc.perform(get("/api/v1/bike-engine-error-codes/error-code/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].errorCodeId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(bikeEngineErrorCodeService.create(any())).thenReturn(linkDTO);

        mockMvc.perform(post("/api/v1/bike-engine-error-codes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(bikeEngineErrorCodeService.update(eq(1L), any())).thenReturn(linkDTO);

        mockMvc.perform(put("/api/v1/bike-engine-error-codes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(bikeEngineErrorCodeService).delete(1L);

        mockMvc.perform(delete("/api/v1/bike-engine-error-codes/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
