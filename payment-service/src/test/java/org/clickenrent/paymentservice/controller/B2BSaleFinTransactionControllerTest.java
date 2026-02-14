package org.clickenrent.paymentservice.controller;

import org.clickenrent.paymentservice.dto.B2BSaleFinTransactionDTO;
import org.clickenrent.paymentservice.dto.FinancialTransactionDTO;
import org.clickenrent.paymentservice.service.B2BSaleFinTransactionService;
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

@WebMvcTest(B2BSaleFinTransactionController.class)
@AutoConfigureMockMvc
class B2BSaleFinTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private B2BSaleFinTransactionService b2bSaleFinTransactionService;

    private B2BSaleFinTransactionDTO b2bSaleFinTransactionDTO;

    @BeforeEach
    void setUp() {
        b2bSaleFinTransactionDTO = B2BSaleFinTransactionDTO.builder()
                .id(1L)
                .b2bSaleExternalId("b2b-sale-ext-123")
                .financialTransaction(FinancialTransactionDTO.builder().id(1L).build())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(b2bSaleFinTransactionService.findAll()).thenReturn(Arrays.asList(b2bSaleFinTransactionDTO));

        mockMvc.perform(get("/api/b2b-sale-fin-transactions").with(csrf()))
                .andExpect(status().isOk());
    }
}




