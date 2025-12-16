package org.clickenrent.rentalservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.rentalservice.dto.B2BSubscriptionDTO;
import org.clickenrent.rentalservice.service.B2BSubscriptionService;
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
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for B2BSubscriptionController.
 */
@WebMvcTest(B2BSubscriptionController.class)
@AutoConfigureMockMvc
class B2BSubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private B2BSubscriptionService b2bSubscriptionService;

    private B2BSubscriptionDTO subscriptionDTO;

    @BeforeEach
    void setUp() {
        subscriptionDTO = B2BSubscriptionDTO.builder()
                .id(1L)
                .externalId("B2BSUB001")
                .locationId(1L)
                .b2bSubscriptionStatusId(2L)
                .endDateTime(LocalDateTime.now().plusYears(1))
                .build();
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAllSubscriptions_WithSuperadminRole_ReturnsOk() throws Exception {
        // Given
        Page<B2BSubscriptionDTO> page = new PageImpl<>(Collections.singletonList(subscriptionDTO));
        when(b2bSubscriptionService.getAllSubscriptions(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/b2b-subscriptions")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].externalId").value("B2BSUB001"));

        verify(b2bSubscriptionService, times(1)).getAllSubscriptions(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllSubscriptions_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        Page<B2BSubscriptionDTO> page = new PageImpl<>(Collections.singletonList(subscriptionDTO));
        when(b2bSubscriptionService.getAllSubscriptions(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/b2b-subscriptions")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(b2bSubscriptionService, times(1)).getAllSubscriptions(any());
    }
    @Test
    @WithMockUser(roles = "B2B")
    void getSubscriptionsByLocation_WithB2BRole_ReturnsOk() throws Exception {
        // Given
        when(b2bSubscriptionService.getSubscriptionsByLocation(1L)).thenReturn(Arrays.asList(subscriptionDTO));

        // When & Then
        mockMvc.perform(get("/api/b2b-subscriptions/by-location/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(b2bSubscriptionService, times(1)).getSubscriptionsByLocation(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSubscriptionById_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(b2bSubscriptionService.getSubscriptionById(1L)).thenReturn(subscriptionDTO);

        // When & Then
        mockMvc.perform(get("/api/b2b-subscriptions/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.externalId").value("B2BSUB001"));

        verify(b2bSubscriptionService, times(1)).getSubscriptionById(1L);
    }

    @Test
    @WithMockUser(roles = "B2B")
    void getSubscriptionById_WithB2BRole_ReturnsOk() throws Exception {
        // Given
        when(b2bSubscriptionService.getSubscriptionById(1L)).thenReturn(subscriptionDTO);

        // When & Then
        mockMvc.perform(get("/api/b2b-subscriptions/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(b2bSubscriptionService, times(1)).getSubscriptionById(1L);
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void createSubscription_WithAdminRole_ReturnsCreated() throws Exception {
        // Given
        B2BSubscriptionDTO newSubscription = B2BSubscriptionDTO.builder()
                .externalId("B2BSUB002")
                .locationId(1L)
                .b2bSubscriptionStatusId(1L)
                .endDateTime(LocalDateTime.now().plusYears(1))
                .build();

        B2BSubscriptionDTO createdSubscription = B2BSubscriptionDTO.builder()
                .id(2L)
                .externalId("B2BSUB002")
                .locationId(1L)
                .b2bSubscriptionStatusId(1L)
                .endDateTime(LocalDateTime.now().plusYears(1))
                .build();

        when(b2bSubscriptionService.createSubscription(any(B2BSubscriptionDTO.class))).thenReturn(createdSubscription);

        // When & Then
        mockMvc.perform(post("/api/b2b-subscriptions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSubscription)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.externalId").value("B2BSUB002"));

        verify(b2bSubscriptionService, times(1)).createSubscription(any(B2BSubscriptionDTO.class));
    }

    @Test
    @WithMockUser(roles = "B2B")
    void createSubscription_WithB2BRole_ReturnsCreated() throws Exception {
        // Given
        when(b2bSubscriptionService.createSubscription(any(B2BSubscriptionDTO.class))).thenReturn(subscriptionDTO);

        // When & Then
        mockMvc.perform(post("/api/b2b-subscriptions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscriptionDTO)))
                .andExpect(status().isCreated());

        verify(b2bSubscriptionService, times(1)).createSubscription(any(B2BSubscriptionDTO.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSubscription_WithAdminRole_ReturnsOk() throws Exception {
        // Given
        when(b2bSubscriptionService.updateSubscription(eq(1L), any(B2BSubscriptionDTO.class))).thenReturn(subscriptionDTO);

        // When & Then
        mockMvc.perform(put("/api/b2b-subscriptions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscriptionDTO)))
                .andExpect(status().isOk());

        verify(b2bSubscriptionService, times(1)).updateSubscription(eq(1L), any(B2BSubscriptionDTO.class));
    }

    @Test
    @WithMockUser(roles = "B2B")
    void updateSubscription_WithB2BRole_ReturnsOk() throws Exception {
        // Given
        when(b2bSubscriptionService.updateSubscription(eq(1L), any(B2BSubscriptionDTO.class))).thenReturn(subscriptionDTO);

        // When & Then
        mockMvc.perform(put("/api/b2b-subscriptions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscriptionDTO)))
                .andExpect(status().isOk());

        verify(b2bSubscriptionService, times(1)).updateSubscription(eq(1L), any(B2BSubscriptionDTO.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSubscription_WithAdminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(b2bSubscriptionService).deleteSubscription(1L);

        // When & Then
        mockMvc.perform(delete("/api/b2b-subscriptions/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(b2bSubscriptionService, times(1)).deleteSubscription(1L);
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteSubscription_WithSuperadminRole_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(b2bSubscriptionService).deleteSubscription(1L);

        // When & Then
        mockMvc.perform(delete("/api/b2b-subscriptions/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(b2bSubscriptionService, times(1)).deleteSubscription(1L);
    }
}
