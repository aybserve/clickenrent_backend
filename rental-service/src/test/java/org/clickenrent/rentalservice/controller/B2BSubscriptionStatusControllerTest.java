package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.B2BSubscriptionStatusDTO;
import org.clickenrent.rentalservice.service.B2BSubscriptionStatusService;
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

@WebMvcTest(B2BSubscriptionStatusController.class)
@AutoConfigureMockMvc
class B2BSubscriptionStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private B2BSubscriptionStatusService b2bSubscriptionStatusService;

    private B2BSubscriptionStatusDTO statusDTO;

    @BeforeEach
    void setUp() {
        statusDTO = B2BSubscriptionStatusDTO.builder()
                .id(1L)
                .name("Active")
                .build();
    }

    @Test
    @WithMockUser(roles = "B2B")
    void getAllStatuses_ReturnsOk() throws Exception {
        when(b2bSubscriptionStatusService.getAllStatuses()).thenReturn(Arrays.asList(statusDTO));

        mockMvc.perform(get("/api/b2b-subscription-statuses").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Active"));
    }

    @Test
    @WithMockUser(roles = "B2B")
    void getStatusById_ReturnsOk() throws Exception {
        when(b2bSubscriptionStatusService.getStatusById(1L)).thenReturn(statusDTO);

        mockMvc.perform(get("/api/b2b-subscription-statuses/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Active"));}
}








