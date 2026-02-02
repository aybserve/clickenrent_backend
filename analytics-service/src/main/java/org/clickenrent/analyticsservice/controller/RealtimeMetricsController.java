package org.clickenrent.analyticsservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.analyticsservice.dto.RealtimeMetricsDTO;
import org.clickenrent.analyticsservice.service.RealtimeMetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for real-time analytics endpoints.
 * Provides current system metrics for live dashboards.
 */
@RestController
@RequestMapping("/api/v1/analytics/realtime")
@RequiredArgsConstructor
@Tag(name = "Real-time Analytics", description = "Real-time metrics for live dashboards")
@SecurityRequirement(name = "bearerAuth")
public class RealtimeMetricsController {

    private final RealtimeMetricsService realtimeMetricsService;

    /**
     * Get real-time system metrics.
     * Returns current status including active rentals, recent activity,
     * available bikes, and support requests.
     * GET /api/v1/analytics/realtime
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get real-time metrics",
            description = "Returns real-time system metrics for live dashboards including: " +
                    "active bike rentals, bike rentals in last 15 minutes, revenue in last 15 minutes, " +
                    "available bikes, and in-progress support requests. " +
                    "Admin users can see all companies, B2B users see their companies. " +
                    "Customer users are not allowed to access this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Real-time metrics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RealtimeMetricsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated or insufficient permissions (customer role)"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - customers cannot access real-time analytics"
            )
    })
    public ResponseEntity<RealtimeMetricsDTO> getRealtimeMetrics() {
        RealtimeMetricsDTO metrics = realtimeMetricsService.getRealtimeMetrics();
        return ResponseEntity.ok(metrics);
    }
}
