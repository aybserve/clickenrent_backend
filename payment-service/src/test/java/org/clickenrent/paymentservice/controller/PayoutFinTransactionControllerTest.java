package org.clickenrent.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.paymentservice.dto.*;
import org.clickenrent.paymentservice.service.PayoutFinTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PayoutFinTransactionController.class)
@AutoConfigureMockMvc
class PayoutFinTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PayoutFinTransactionService payoutFinTransactionService;

    private PayoutFinTransactionDTO payoutFinTransactionDTO;

    @BeforeEach
    void setUp() {
        payoutFinTransactionDTO = PayoutFinTransactionDTO.builder()
                .id(1L)
                .b2bRevenueSharePayout(B2BRevenueSharePayoutDTO.builder().id(1L).build())
                .financialTransaction(FinancialTransactionDTO.builder().id(1L).build())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(payoutFinTransactionService.findAll()).thenReturn(Arrays.asList(payoutFinTransactionDTO));

        mockMvc.perform(get("/api/payout-fin-transactions").with(csrf()))
                .andExpect(status().isOk());
    }
}


