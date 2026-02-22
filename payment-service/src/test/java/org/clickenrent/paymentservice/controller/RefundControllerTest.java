package org.clickenrent.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.paymentservice.dto.CreateRefundRequestDTO;
import org.clickenrent.paymentservice.dto.RefundDTO;
import org.clickenrent.paymentservice.service.RefundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.clickenrent.paymentservice.config.SecurityConfig;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RefundController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class RefundControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RefundService refundService;

    private RefundDTO refundDTO;
    private CreateRefundRequestDTO createRequest;

    @BeforeEach
    void setUp() {
        refundDTO = RefundDTO.builder()
                .id(1L)
                .externalId("refund-ext-1")
                .financialTransactionId(100L)
                .amount(new BigDecimal("25.00"))
                .build();
        createRequest = CreateRefundRequestDTO.builder()
                .financialTransactionId(100L)
                .amount(new BigDecimal("25.00"))
                .currencyCode("EUR")
                .refundReasonCode("CUSTOMER_REQUEST")
                .description("Customer requested refund")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(refundService.findAll()).thenReturn(Arrays.asList(refundDTO));

        mockMvc.perform(get("/api/v1/refunds").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(refundService.findById(1L)).thenReturn(refundDTO);

        mockMvc.perform(get("/api/v1/refunds/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByExternalId_ReturnsOk() throws Exception {
        when(refundService.findByExternalId("refund-ext-1")).thenReturn(refundDTO);

        mockMvc.perform(get("/api/v1/refunds/external/refund-ext-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value("refund-ext-1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByFinancialTransactionId_ReturnsOk() throws Exception {
        when(refundService.findByFinancialTransactionId(100L)).thenReturn(Arrays.asList(refundDTO));

        mockMvc.perform(get("/api/v1/refunds/transaction/100").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].financialTransactionId").value(100));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRefund_ReturnsCreated() throws Exception {
        when(refundService.createRefund(any(CreateRefundRequestDTO.class))).thenReturn(refundDTO);

        mockMvc.perform(post("/api/v1/refunds")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createRefund_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/refunds")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
        verify(refundService, never()).createRefund(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRefundStatus_ReturnsOk() throws Exception {
        when(refundService.updateRefundStatus(eq(1L), eq("SUCCEEDED"))).thenReturn(refundDTO);

        mockMvc.perform(put("/api/v1/refunds/1/status").with(csrf()).param("statusCode", "SUCCEEDED"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteByExternalId_ReturnsNoContent() throws Exception {
        doNothing().when(refundService).deleteByExternalId("refund-ext-1");

        mockMvc.perform(delete("/api/v1/refunds/external/refund-ext-1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
