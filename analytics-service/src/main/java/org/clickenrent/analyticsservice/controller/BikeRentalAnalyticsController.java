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
import org.clickenrent.analyticsservice.dto.BikeRentalAnalyticsDTO;
import org.clickenrent.analyticsservice.service.BikeRentalAnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * REST controller for bike rental analytics endpoints.
 * Provides rental patterns, trends, peak times, and bike type breakdown.
 */
@RestController
@RequestMapping("/api/v1/analytics/bike-rentals")
@RequiredArgsConstructor
@Tag(name = "Rental Analytics", description = "Bike rental patterns and trends")
@SecurityRequirement(name = "bearerAuth")
public class BikeRentalAnalyticsController {

    private final BikeRentalAnalyticsService bikeRentalAnalyticsService;

    /**
     * Get bike rental analytics with patterns and trends.
     * Returns summary statistics, duration metrics, peak hours/days, and bike type breakdown.
     * GET /api/v1/analytics/bike-rentals
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get bike rental analytics",
            description = "Returns rental patterns and trends including summary statistics, " +
                    "duration metrics (min, max, average), peak hours (top 3), peak days (top 2), " +
                    "and bike type breakdown. Admin users can see all companies, B2B users see their companies. " +
                    "Customer users are not allowed to access this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rental analytics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BikeRentalAnalyticsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters (e.g., missing dates, start date after end date)"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated or insufficient permissions (customer role)"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - customers cannot access rental analytics"
            )
    })
    public ResponseEntity<BikeRentalAnalyticsDTO> getBikeRentalAnalytics(
            @Parameter(description = "Start date (format: yyyy-MM-dd, required)", required = true)
            @RequestParam(required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @Parameter(description = "End date (format: yyyy-MM-dd, required)", required = true)
            @RequestParam(required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @Parameter(description = "Grouping parameter: day, week, month, hour (optional, future feature)")
            @RequestParam(required = false)
            String groupBy) {

        BikeRentalAnalyticsDTO analytics = bikeRentalAnalyticsService.getBikeRentalAnalytics(from, to, groupBy);
        return ResponseEntity.ok(analytics);
    }
}
