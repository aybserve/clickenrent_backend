package org.clickenrent.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.clickenrent.paymentservice.dto.B2BRevenueSharePayoutDTO;
import org.clickenrent.paymentservice.dto.PaymentStatusDTO;
import org.clickenrent.paymentservice.service.B2BRevenueSharePayoutService;
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
import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(B2BRevenueSharePayoutController.class)
@AutoConfigureMockMvc
@org.springframework.context.annotation.Import(org.clickenrent.paymentservice.config.SecurityConfig.class)
@org.springframework.test.context.TestPropertySource(properties = "jwt.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLW11c3QtYmUtbG9uZy1lbm91Z2g=")
class B2BRevenueSharePayoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private B2BRevenueSharePayoutService b2bRevenueSharePayoutService;

    @MockBean
    private SecurityService securityService;

    private B2BRevenueSharePayoutDTO payoutDTO;

    @BeforeEach
    void setUp() {
        payoutDTO = B2BRevenueSharePayoutDTO.builder()
                .id(1L)
                .companyExternalId("company-ext-123")
                .paymentStatus(PaymentStatusDTO.builder().code("PENDING").build())
                .dueDate(LocalDate.now())
                .totalAmount(new BigDecimal("1000.00"))
                .paidAmount(BigDecimal.ZERO)
                .remainingAmount(new BigDecimal("1000.00"))
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_ReturnsOk() throws Exception {
        when(b2bRevenueSharePayoutService.findAll()).thenReturn(Arrays.asList(payoutDTO));

        mockMvc.perform(get("/api/b2b-revenue-share-payouts").with(csrf()))
                .andExpect(status().isOk());
    }
}




