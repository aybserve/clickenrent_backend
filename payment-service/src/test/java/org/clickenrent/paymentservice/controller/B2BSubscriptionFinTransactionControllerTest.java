package org.clickenrent.paymentservice.controller;

import org.clickenrent.paymentservice.dto.B2BSubscriptionFinTransactionDTO;
import org.clickenrent.paymentservice.dto.FinancialTransactionDTO;
import org.clickenrent.paymentservice.service.B2BSubscriptionFinTransactionService;
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

@WebMvcTest(B2BSubscriptionFinTransactionController.class)
@AutoConfigureMockMvc
class B2BSubscriptionFinTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private B2BSubscriptionFinTransactionService b2bSubscriptionFinTransactionService;

    private B2BSubscriptionFinTransactionDTO b2bSubscriptionFinTransactionDTO;

    @BeforeEach
    void setUp() {
        b2bSubscriptionFinTransactionDTO = B2BSubscriptionFinTransactionDTO.builder()
                .id(1L)
                .b2bSubscriptionId(1L)
                .financialTransaction(FinancialTransactionDTO.builder().id(1L).build())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(b2bSubscriptionFinTransactionService.findAll()).thenReturn(Arrays.asList(b2bSubscriptionFinTransactionDTO));

        mockMvc.perform(get("/api/b2b-subscription-fin-transactions").with(csrf()))
                .andExpect(status().isOk());
    }
}

