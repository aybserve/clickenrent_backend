package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.FeedbackDTO;
import org.clickenrent.supportservice.service.FeedbackService;
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

@WebMvcTest(FeedbackController.class)
@AutoConfigureMockMvc
class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FeedbackService feedbackService;

    private FeedbackDTO feedbackDTO;

    @BeforeEach
    void setUp() {
        feedbackDTO = FeedbackDTO.builder()
                .id(1L)
                .externalId("550e8400-e29b-41d4-a716-446655440301")
                .userId(1L)
                .rate(5)
                .comment("Excellent service")
                .dateTime(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(feedbackService.getAll()).thenReturn(Arrays.asList(feedbackDTO));

        mockMvc.perform(get("/api/feedbacks").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rate").value(5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(feedbackService.getById(1L)).thenReturn(feedbackDTO);

        mockMvc.perform(get("/api/feedbacks/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rate").value(5));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void create_ReturnsCreated() throws Exception {
        when(feedbackService.create(any())).thenReturn(feedbackDTO);

        mockMvc.perform(post("/api/feedbacks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void update_ReturnsOk() throws Exception {
        when(feedbackService.update(eq(1L), any())).thenReturn(feedbackDTO);

        mockMvc.perform(put("/api/feedbacks/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(feedbackService).delete(1L);

        mockMvc.perform(delete("/api/feedbacks/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}


