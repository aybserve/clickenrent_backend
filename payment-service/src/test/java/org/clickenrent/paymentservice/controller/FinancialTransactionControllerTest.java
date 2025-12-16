package org.clickenrent.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.paymentservice.dto.*;
import org.clickenrent.paymentservice.service.FinancialTransactionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FinancialTransactionController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class FinancialTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FinancialTransactionService financialTransactionService;

    private FinancialTransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        transactionDTO = FinancialTransactionDTO.builder()
                .id(1L)
                .payerId(1L)
                .recipientId(2L)
                .amount(new BigDecimal("100.00"))
                .currency(CurrencyDTO.builder().code("USD").build())
                .paymentMethod(PaymentMethodDTO.builder().code("CREDIT_CARD").build())
                .paymentStatus(PaymentStatusDTO.builder().code("SUCCEEDED").build())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(financialTransactionService.findAll()).thenReturn(Arrays.asList(transactionDTO));

        mockMvc.perform(get("/api/financial-transactions").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ReturnsOk() throws Exception {
        when(financialTransactionService.findById(1L)).thenReturn(transactionDTO);

        mockMvc.perform(get("/api/financial-transactions/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(financialTransactionService.create(any())).thenReturn(transactionDTO);

        mockMvc.perform(post("/api/financial-transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void create_WithCustomerRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/financial-transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isForbidden());
    }
}
