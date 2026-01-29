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
import org.clickenrent.analyticsservice.dto.DashboardOverviewDTO;
import org.clickenrent.analyticsservice.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * REST controller for dashboard overview and KPI endpoints.
 * Provides aggregated analytics for dashboard visualization.
 */
@RestController
@RequestMapping("/api/v1/analytics/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard overview and KPI endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Get dashboard overview with aggregated KPIs.
     * Returns key performance indicators for the dashboard with period comparison.
     * GET /api/v1/analytics/dashboard/overview
     */
    @GetMapping("/overview")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get dashboard overview",
            description = "Returns key performance indicators for the dashboard overview. " +
                    "Includes comparison with previous period if enabled. " +
                    "Admin users can see all companies, B2B users see their companies, " +
                    "Customer users are not allowed to access this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dashboard overview retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DashboardOverviewDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters (e.g., start date after end date)"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Not authenticated or insufficient permissions (customer role)"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - customers cannot access dashboard analytics"
            )
    })
    public ResponseEntity<DashboardOverviewDTO> getDashboardOverview(
            @Parameter(description = "Start date (format: yyyy-MM-dd, default: 30 days ago)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @Parameter(description = "End date (format: yyyy-MM-dd, default: today)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @Parameter(description = "Include comparison with previous period (default: true)")
            @RequestParam(required = false, defaultValue = "true")
            Boolean compareWithPrevious) {

        DashboardOverviewDTO overview = dashboardService.getDashboardOverview(from, to, compareWithPrevious);
        return ResponseEntity.ok(overview);
    }
}
