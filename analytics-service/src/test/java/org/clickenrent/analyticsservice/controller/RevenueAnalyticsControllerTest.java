package org.clickenrent.analyticsservice.controller;

import org.clickenrent.analyticsservice.config.SecurityConfig;
import org.clickenrent.analyticsservice.dto.RevenueAnalyticsDTO;
import org.clickenrent.analyticsservice.dto.RevenueSummaryDTO;
import org.clickenrent.analyticsservice.service.RevenueAnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for RevenueAnalyticsController.
 */
@WebMvcTest(RevenueAnalyticsController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "jwt.secret=dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLTI1Ni1iaXQ=",
        "sentry.dsn="
})
class RevenueAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RevenueAnalyticsService revenueAnalyticsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRevenueAnalytics_WithAdminRole_ReturnsOk() throws Exception {
        RevenueAnalyticsDTO analytics = RevenueAnalyticsDTO.builder()
                .summary(RevenueSummaryDTO.builder()
                        .totalRevenue(BigDecimal.valueOf(1000.00))
                        .totalEarnings(BigDecimal.valueOf(800.00))
                        .totalRefunds(BigDecimal.ZERO)
                        .build())
                .topLocations(Collections.emptyList())
                .build();
        when(revenueAnalyticsService.getRevenueAnalytics(any(), any())).thenReturn(analytics);

        mockMvc.perform(get("/api/v1/analytics/revenue")
                        .with(csrf())
                        .param("from", "2025-01-01")
                        .param("to", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").exists())
                .andExpect(jsonPath("$.topLocations").isArray());

        verify(revenueAnalyticsService).getRevenueAnalytics(any(), any());
    }

    @Test
    void getRevenueAnalytics_WithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/revenue")
                        .with(csrf())
                        .param("from", "2025-01-01")
                        .param("to", "2025-01-31"))
                .andExpect(status().isUnauthorized());
    }
}
