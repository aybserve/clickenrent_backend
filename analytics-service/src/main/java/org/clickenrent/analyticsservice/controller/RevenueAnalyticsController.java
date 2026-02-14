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
import org.clickenrent.analyticsservice.dto.RevenueAnalyticsDTO;
import org.clickenrent.analyticsservice.service.RevenueAnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * REST controller for revenue analytics endpoints.
 * Provides revenue breakdown, earnings calculation, refunds tracking, and top locations.
 */
@RestController
@RequestMapping("/api/v1/analytics/revenue")
@RequiredArgsConstructor
@Tag(name = "Revenue Analytics", description = "Revenue and earnings analytics")
@SecurityRequirement(name = "bearerAuth")
public class RevenueAnalyticsController {

    private final RevenueAnalyticsService revenueAnalyticsService;

    /**
     * Get revenue analytics with earnings and refunds.
     * Returns summary statistics (total revenue, earnings, refunds) and top locations breakdown.
     * GET /api/v1/analytics/revenue
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get revenue analytics",
            description = "Returns revenue analytics including total revenue (sum of all rental prices), " +
                    "total earnings (company's revenue share from rentals), total refunds (from payment transactions), " +
                    "and top 5 locations by revenue. Admin users can see all companies, B2B users see their companies. " +
                    "Customer users are not allowed to access this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Revenue analytics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RevenueAnalyticsDTO.class))
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
                    description = "Access denied - customers cannot access revenue analytics"
            )
    })
    public ResponseEntity<RevenueAnalyticsDTO> getRevenueAnalytics(
            @Parameter(description = "Start date (format: yyyy-MM-dd, required)", required = true)
            @RequestParam(required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @Parameter(description = "End date (format: yyyy-MM-dd, required)", required = true)
            @RequestParam(required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        RevenueAnalyticsDTO analytics = revenueAnalyticsService.getRevenueAnalytics(from, to);
        return ResponseEntity.ok(analytics);
    }
}
