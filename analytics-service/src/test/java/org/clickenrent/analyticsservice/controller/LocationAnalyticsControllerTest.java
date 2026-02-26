package org.clickenrent.analyticsservice.controller;

import org.clickenrent.analyticsservice.client.AuthServiceClient;
import org.clickenrent.analyticsservice.client.PaymentServiceClient;
import org.clickenrent.analyticsservice.client.RentalServiceClient;
import org.clickenrent.analyticsservice.client.SupportServiceClient;
import org.clickenrent.analyticsservice.config.SecurityConfig;
import org.clickenrent.analyticsservice.dto.LocationAnalyticsDTO;
import org.clickenrent.analyticsservice.dto.LocationSummaryDTO;
import org.clickenrent.analyticsservice.service.LocationAnalyticsService;
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

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for LocationAnalyticsController.
 */
@WebMvcTest(LocationAnalyticsController.class)
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
class LocationAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationAnalyticsService locationAnalyticsService;

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
    void getLocationAnalytics_WithAdminRole_ReturnsOk() throws Exception {
        LocationAnalyticsDTO analytics = LocationAnalyticsDTO.builder()
                .summary(LocationSummaryDTO.builder()
                        .totalLocations(5)
                        .activeLocations(4)
                        .inactiveLocations(1)
                        .build())
                .locations(Collections.emptyList())
                .build();
        when(locationAnalyticsService.getLocationAnalytics()).thenReturn(analytics);

        mockMvc.perform(get("/api/v1/analytics/locations")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.totalLocations").value(5))
                .andExpect(jsonPath("$.summary.activeLocations").value(4))
                .andExpect(jsonPath("$.locations").isArray());

        verify(locationAnalyticsService).getLocationAnalytics();
    }

    @Test
    void getLocationAnalytics_WithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/locations")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
