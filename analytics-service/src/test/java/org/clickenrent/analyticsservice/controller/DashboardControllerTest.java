package org.clickenrent.analyticsservice.controller;

import org.clickenrent.analyticsservice.client.AuthServiceClient;
import org.clickenrent.analyticsservice.client.PaymentServiceClient;
import org.clickenrent.analyticsservice.client.RentalServiceClient;
import org.clickenrent.analyticsservice.client.SupportServiceClient;
import org.clickenrent.analyticsservice.config.SecurityConfig;
import org.clickenrent.analyticsservice.dto.DashboardOverviewDTO;
import org.clickenrent.analyticsservice.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for DashboardController.
 */
@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLTI1Ni1iaXQ=",
        "sentry.dsn=",
        "eureka.client.enabled=false",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"
})
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private AuthServiceClient authServiceClient;

    @MockBean
    private RentalServiceClient rentalServiceClient;

    @MockBean
    private PaymentServiceClient paymentServiceClient;

    @MockBean
    private SupportServiceClient supportServiceClient;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDashboardOverview_WithAdminRole_ReturnsOk() throws Exception {
        DashboardOverviewDTO overview = DashboardOverviewDTO.builder()
                .generatedAt(ZonedDateTime.now())
                .build();
        when(dashboardService.getDashboardOverview(any(), any(), eq(true))).thenReturn(overview);

        mockMvc.perform(get("/api/v1/analytics/dashboard/overview")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generatedAt").exists());

        verify(dashboardService).getDashboardOverview(any(), any(), eq(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDashboardOverview_WithFromToParams_CallsServiceWithParams() throws Exception {
        DashboardOverviewDTO overview = DashboardOverviewDTO.builder()
                .generatedAt(ZonedDateTime.now())
                .build();
        when(dashboardService.getDashboardOverview(any(), any(), eq(false))).thenReturn(overview);

        mockMvc.perform(get("/api/v1/analytics/dashboard/overview")
                        .with(csrf())
                        .param("from", "2025-01-01")
                        .param("to", "2025-01-31")
                        .param("compareWithPrevious", "false"))
                .andExpect(status().isOk());

        verify(dashboardService).getDashboardOverview(any(), any(), eq(false));
    }

    @Test
    void getDashboardOverview_WithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/dashboard/overview")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
