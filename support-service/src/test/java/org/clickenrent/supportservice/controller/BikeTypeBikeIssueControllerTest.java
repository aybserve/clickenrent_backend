package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.BikeTypeBikeIssueDTO;
import org.clickenrent.supportservice.service.BikeTypeBikeIssueService;
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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BikeTypeBikeIssueController.class)
@AutoConfigureMockMvc
class BikeTypeBikeIssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeTypeBikeIssueService bikeTypeBikeIssueService;

    private BikeTypeBikeIssueDTO junctionDTO;

    @BeforeEach
    void setUp() {
        junctionDTO = BikeTypeBikeIssueDTO.builder()
                .id(1L)
                .bikeTypeExternalId("bike-type-uuid-1")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(bikeTypeBikeIssueService.getAll()).thenReturn(Arrays.asList(junctionDTO));

        mockMvc.perform(get("/api/bike-type-bike-issues").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bikeTypeExternalId").value("bike-type-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(bikeTypeBikeIssueService.getById(1L)).thenReturn(junctionDTO);

        mockMvc.perform(get("/api/bike-type-bike-issues/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bikeTypeExternalId").value("bike-type-uuid-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(bikeTypeBikeIssueService.create(any())).thenReturn(junctionDTO);

        mockMvc.perform(post("/api/bike-type-bike-issues")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(junctionDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(bikeTypeBikeIssueService).delete(1L);

        mockMvc.perform(delete("/api/bike-type-bike-issues/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}




