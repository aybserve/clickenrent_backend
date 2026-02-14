package org.clickenrent.analyticsservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.analyticsservice.dto.LocationAnalyticsDTO;
import org.clickenrent.analyticsservice.service.LocationAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for location analytics endpoints.
 * Provides location summary statistics and performance metrics.
 */
@RestController
@RequestMapping("/api/v1/analytics/locations")
@RequiredArgsConstructor
@Tag(name = "Location Analytics", description = "Location performance metrics")
@SecurityRequirement(name = "bearerAuth")
public class LocationAnalyticsController {

    private final LocationAnalyticsService locationAnalyticsService;

    /**
     * Get location analytics with summary and full locations list.
     * Returns location counts (total, active, inactive) and all location details.
     * GET /api/v1/analytics/locations
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get location analytics",
            description = "Returns location analytics including summary statistics (total locations, active, inactive) " +
                    "and full list of all locations belonging to the company. " +
                    "Admin users can see all companies, B2B users see their companies. " +
                    "Customer users are not allowed to access this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Location analytics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LocationAnalyticsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated or insufficient permissions (customer role)"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - customers cannot access location analytics"
            )
    })
    public ResponseEntity<LocationAnalyticsDTO> getLocationAnalytics() {
        LocationAnalyticsDTO analytics = locationAnalyticsService.getLocationAnalytics();
        return ResponseEntity.ok(analytics);
    }
}
