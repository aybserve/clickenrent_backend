package org.clickenrent.analyticsservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.analyticsservice.dto.FleetAnalyticsDTO;
import org.clickenrent.analyticsservice.service.FleetAnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * REST controller for fleet analytics endpoints.
 * Provides bike fleet status, battery levels, and inspection schedule information.
 */
@RestController
@RequestMapping("/api/v1/analytics/fleet")
@RequiredArgsConstructor
@Tag(name = "Fleet Analytics", description = "Bike fleet status and utilization")
@SecurityRequirement(name = "bearerAuth")
public class FleetAnalyticsController {

    private final FleetAnalyticsService fleetAnalyticsService;

    /**
     * Get fleet analytics with current status and battery levels.
     * Returns bike counts by status (available, in use, reserved, broken, disabled),
     * battery level distribution, and inspection schedule.
     * GET /api/v1/analytics/fleet
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get fleet analytics",
            description = "Returns bike fleet analytics including current status (total bikes, counts by status), " +
                    "battery level distribution (full, medium, low, critical), and bike inspection schedule. " +
                    "Admin users can see all companies, B2B users see their companies. " +
                    "Customer users are not allowed to access this endpoint. " +
                    "Date parameters (from/to) are optional and reserved for future trend analysis."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Fleet analytics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = FleetAnalyticsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated or insufficient permissions (customer role)"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - customers cannot access fleet analytics"
            )
    })
    public ResponseEntity<FleetAnalyticsDTO> getFleetAnalytics(
            @Parameter(description = "Start date (format: yyyy-MM-dd, optional, reserved for future trends)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @Parameter(description = "End date (format: yyyy-MM-dd, optional, reserved for future trends)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        FleetAnalyticsDTO analytics = fleetAnalyticsService.getFleetAnalytics(from, to);
        return ResponseEntity.ok(analytics);
    }
}
