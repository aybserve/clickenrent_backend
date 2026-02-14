package org.clickenrent.analyticsservice.client;

import org.clickenrent.analyticsservice.dto.SupportRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Feign client for communicating with support-service.
 * Used to fetch support request data for analytics.
 */
@FeignClient(
    name = "support-service",
    path = "/api/v1"
)
public interface SupportServiceClient {

    /**
     * Get all support requests.
     * The support-service will automatically filter by the user's company via security context.
     *
     * @return List of support requests
     */
    @GetMapping("/support-requests")
    List<SupportRequestDTO> getSupportRequests();
}
