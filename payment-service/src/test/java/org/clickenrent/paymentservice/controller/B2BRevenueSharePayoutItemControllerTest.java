package org.clickenrent.paymentservice.controller;

import org.clickenrent.paymentservice.dto.B2BRevenueSharePayoutItemDTO;
import org.clickenrent.paymentservice.service.B2BRevenueSharePayoutItemService;
import org.clickenrent.paymentservice.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(B2BRevenueSharePayoutItemController.class)
@AutoConfigureMockMvc
@org.springframework.context.annotation.Import(org.clickenrent.paymentservice.config.SecurityConfig.class)
@org.springframework.test.context.TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class B2BRevenueSharePayoutItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private B2BRevenueSharePayoutItemService payoutItemService;

    @MockBean
    private SecurityService securityService;

    private B2BRevenueSharePayoutItemDTO payoutItemDTO;

    @BeforeEach
    void setUp() {
        payoutItemDTO = B2BRevenueSharePayoutItemDTO.builder()
                .id(1L)
                .b2bRevenueSharePayoutId(1L)
                .bikeRentalExternalId("bike-rental-ext-123")
                .amount(new BigDecimal("50.00"))
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(payoutItemService.findAll()).thenReturn(Arrays.asList(payoutItemDTO));

        mockMvc.perform(get("/api/b2b-revenue-share-payout-items").with(csrf()))
                .andExpect(status().isOk());
    }
}




