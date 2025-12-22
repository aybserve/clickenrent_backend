package org.clickenrent.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.paymentservice.dto.FinancialTransactionDTO;
import org.clickenrent.paymentservice.dto.RentalFinTransactionDTO;
import org.clickenrent.paymentservice.service.RentalFinTransactionService;
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

@WebMvcTest(RentalFinTransactionController.class)
@AutoConfigureMockMvc
class RentalFinTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RentalFinTransactionService rentalFinTransactionService;

    private RentalFinTransactionDTO rentalFinTransactionDTO;

    @BeforeEach
    void setUp() {
        rentalFinTransactionDTO = RentalFinTransactionDTO.builder()
                .id(1L)
                .rentalExternalId("rental-ext-123")
                .bikeRentalExternalId("bike-rental-ext-456")
                .financialTransaction(FinancialTransactionDTO.builder().id(1L).build())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(rentalFinTransactionService.findAll()).thenReturn(Arrays.asList(rentalFinTransactionDTO));

        mockMvc.perform(get("/api/rental-fin-transactions").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ReturnsCreated() throws Exception {
        when(rentalFinTransactionService.create(any())).thenReturn(rentalFinTransactionDTO);

        mockMvc.perform(post("/api/rental-fin-transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalFinTransactionDTO)))
                .andExpect(status().isCreated());
    }
}




