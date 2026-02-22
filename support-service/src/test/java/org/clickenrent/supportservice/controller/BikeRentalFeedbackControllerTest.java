package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.contracts.support.BikeRentalFeedbackDTO;
import org.clickenrent.supportservice.service.BikeRentalFeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BikeRentalFeedbackController.class)
@AutoConfigureMockMvc
class BikeRentalFeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BikeRentalFeedbackService bikeRentalFeedbackService;

    private BikeRentalFeedbackDTO feedbackDTO;

    @BeforeEach
    void setUp() {
        feedbackDTO = BikeRentalFeedbackDTO.builder()
                .id(1L)
                .userExternalId("user-uuid-1")
                .bikeRentalExternalId("bike-rental-uuid-101")
                .rate(5)
                .comment("Great bike!")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(bikeRentalFeedbackService.getAll()).thenReturn(Arrays.asList(feedbackDTO));

        mockMvc.perform(get("/api/v1/bike-rental-feedbacks").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rate").value(5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(bikeRentalFeedbackService.getById(1L)).thenReturn(feedbackDTO);

        mockMvc.perform(get("/api/v1/bike-rental-feedbacks/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rate").value(5));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void create_ReturnsCreated() throws Exception {
        when(bikeRentalFeedbackService.create(any())).thenReturn(feedbackDTO);

        mockMvc.perform(post("/api/v1/bike-rental-feedbacks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void update_ReturnsOk() throws Exception {
        when(bikeRentalFeedbackService.update(eq(1L), any())).thenReturn(feedbackDTO);

        mockMvc.perform(put("/api/v1/bike-rental-feedbacks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(bikeRentalFeedbackService).delete(1L);

        mockMvc.perform(delete("/api/v1/bike-rental-feedbacks/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}








