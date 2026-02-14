package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.BikeIssueDTO;
import org.clickenrent.supportservice.service.BikeIssueService;
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

@WebMvcTest(BikeIssueController.class)
@AutoConfigureMockMvc
class BikeIssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeIssueService bikeIssueService;

    private BikeIssueDTO bikeIssueDTO;

    @BeforeEach
    void setUp() {
        bikeIssueDTO = BikeIssueDTO.builder()
                .id(1L)
                .externalId("550e8400-e29b-41d4-a716-446655440101")
                .name("Battery Issues")
                .description("Problems related to bike battery")
                .isFixableByClient(false)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(bikeIssueService.getAll()).thenReturn(Arrays.asList(bikeIssueDTO));

        mockMvc.perform(get("/api/bike-issues").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Battery Issues"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(bikeIssueService.getById(1L)).thenReturn(bikeIssueDTO);

        mockMvc.perform(get("/api/bike-issues/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Battery Issues"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByExternalId_ReturnsOk() throws Exception {
        when(bikeIssueService.getByExternalId("550e8400-e29b-41d4-a716-446655440101"))
                .thenReturn(bikeIssueDTO);

        mockMvc.perform(get("/api/bike-issues/external/550e8400-e29b-41d4-a716-446655440101").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Battery Issues"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRootIssues_ReturnsOk() throws Exception {
        when(bikeIssueService.getRootIssues()).thenReturn(Arrays.asList(bikeIssueDTO));

        mockMvc.perform(get("/api/bike-issues/root").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Battery Issues"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSubIssues_ReturnsOk() throws Exception {
        when(bikeIssueService.getSubIssues(1L)).thenReturn(Arrays.asList(bikeIssueDTO));

        mockMvc.perform(get("/api/bike-issues/parent/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Battery Issues"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(bikeIssueService.create(any())).thenReturn(bikeIssueDTO);

        mockMvc.perform(post("/api/bike-issues")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeIssueDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(bikeIssueService.update(eq(1L), any())).thenReturn(bikeIssueDTO);

        mockMvc.perform(put("/api/bike-issues/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bikeIssueDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(bikeIssueService).delete(1L);

        mockMvc.perform(delete("/api/bike-issues/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}








