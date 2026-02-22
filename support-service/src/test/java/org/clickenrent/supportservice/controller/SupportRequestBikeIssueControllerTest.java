package org.clickenrent.supportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.supportservice.dto.SupportRequestBikeIssueDTO;
import org.clickenrent.supportservice.service.SupportRequestBikeIssueService;
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

@WebMvcTest(SupportRequestBikeIssueController.class)
@AutoConfigureMockMvc
class SupportRequestBikeIssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SupportRequestBikeIssueService supportRequestBikeIssueService;

    private SupportRequestBikeIssueDTO junctionDTO;

    @BeforeEach
    void setUp() {
        junctionDTO = SupportRequestBikeIssueDTO.builder()
                .id(1L)
                .externalId("junction-uuid-1")
                .supportRequestId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(supportRequestBikeIssueService.getAll()).thenReturn(Arrays.asList(junctionDTO));

        mockMvc.perform(get("/api/v1/support-request-bike-issues").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].supportRequestId").value(1));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getById_ReturnsOk() throws Exception {
        when(supportRequestBikeIssueService.getById(1L)).thenReturn(junctionDTO);

        mockMvc.perform(get("/api/v1/support-request-bike-issues/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supportRequestId").value(1));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void create_ReturnsCreated() throws Exception {
        when(supportRequestBikeIssueService.create(any())).thenReturn(junctionDTO);

        mockMvc.perform(post("/api/v1/support-request-bike-issues")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(junctionDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(supportRequestBikeIssueService).delete(1L);

        mockMvc.perform(delete("/api/v1/support-request-bike-issues/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}








