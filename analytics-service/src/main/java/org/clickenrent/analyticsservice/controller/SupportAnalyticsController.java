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
import org.clickenrent.analyticsservice.dto.SupportAnalyticsDTO;
import org.clickenrent.analyticsservice.service.SupportAnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * REST controller for support ticket analytics endpoints.
 * Provides support request statistics and metrics.
 */
@RestController
@RequestMapping("/api/v1/analytics/support")
@RequiredArgsConstructor
@Tag(name = "Support Analytics", description = "Customer support metrics")
@SecurityRequirement(name = "bearerAuth")
public class SupportAnalyticsController {

    private final SupportAnalyticsService supportAnalyticsService;

    /**
     * Get support ticket analytics with status breakdown.
     * Returns summary statistics (total, open, in progress, resolved) for support requests.
     * GET /api/v1/analytics/support
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get support requests analytics",
            description = "Returns support requests analytics including summary statistics " +
                    "(total support requests, open, in progress, resolved) for the specified date range. " +
                    "Filters support requests by dateCreated field. " +
                    "Admin users can see all companies, B2B users see their companies. " +
                    "Customer users are not allowed to access this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Support analytics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SupportAnalyticsDTO.class))
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
                    description = "Access denied - customers cannot access support analytics"
            )
    })
    public ResponseEntity<SupportAnalyticsDTO> getSupportAnalytics(
            @Parameter(description = "Start date (format: yyyy-MM-dd, required)", required = true)
            @RequestParam(required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @Parameter(description = "End date (format: yyyy-MM-dd, required)", required = true)
            @RequestParam(required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        SupportAnalyticsDTO analytics = supportAnalyticsService.getSupportAnalytics(from, to);
        return ResponseEntity.ok(analytics);
    }
}
