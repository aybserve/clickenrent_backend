package org.clickenrent.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.paymentservice.dto.PaymentStatusDTO;
import org.clickenrent.paymentservice.service.PaymentStatusService;
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

@WebMvcTest(PaymentStatusController.class)
@AutoConfigureMockMvc
class PaymentStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentStatusService paymentStatusService;

    private PaymentStatusDTO paymentStatusDTO;

    @BeforeEach
    void setUp() {
        paymentStatusDTO = PaymentStatusDTO.builder()
                .id(1L)
                .code("SUCCEEDED")
                .name("Payment Succeeded")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(paymentStatusService.findAll()).thenReturn(Arrays.asList(paymentStatusDTO));

        mockMvc.perform(get("/api/payment-statuses").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("SUCCEEDED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(paymentStatusService.findById(1L)).thenReturn(paymentStatusDTO);

        mockMvc.perform(get("/api/payment-statuses/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCEEDED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(paymentStatusService.create(any())).thenReturn(paymentStatusDTO);

        mockMvc.perform(post("/api/payment-statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentStatusDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ReturnsOk() throws Exception {
        when(paymentStatusService.update(eq(1L), any())).thenReturn(paymentStatusDTO);

        mockMvc.perform(put("/api/payment-statuses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentStatusDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ReturnsNoContent() throws Exception {
        doNothing().when(paymentStatusService).delete(1L);

        mockMvc.perform(delete("/api/payment-statuses/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}








