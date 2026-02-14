package org.clickenrent.analyticsservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.analyticsservice.dto.AnalyticsHourlyMetricsDTO;
import org.clickenrent.analyticsservice.service.AnalyticsHourlyMetricsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * REST controller for Analytics Hourly Metrics management operations.
 * Provides endpoints for querying and managing aggregated hourly analytics.
 */
@RestController
@RequestMapping("/api/v1/analytics/hourly-metrics")
@RequiredArgsConstructor
@Tag(name = "Analytics Hourly Metrics", description = "Hourly aggregated analytics endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsHourlyMetricsController {

    private final AnalyticsHourlyMetricsService service;

    /**
     * Get all hourly metrics with pagination.
     * GET /api/v1/analytics/hourly-metrics
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all hourly metrics",
            description = "Returns a paginated list of hourly analytics metrics. Access control: Admin sees all, B2B sees company metrics."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<AnalyticsHourlyMetricsDTO>> getAllMetrics(
            @PageableDefault(size = 20, sort = "metricHour", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AnalyticsHourlyMetricsDTO> metrics = service.getAllMetrics(pageable);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by ID.
     * GET /api/v1/analytics/hourly-metrics/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get hourly metrics by ID",
            description = "Returns hourly analytics metrics by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics found",
                    content = @Content(schema = @Schema(implementation = AnalyticsHourlyMetricsDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Metrics not found")
    })
    public ResponseEntity<AnalyticsHourlyMetricsDTO> getMetricsById(
            @Parameter(description = "Metrics ID", required = true) @PathVariable Long id) {
        AnalyticsHourlyMetricsDTO metrics = service.getMetricsById(id);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by external ID.
     * GET /api/v1/analytics/hourly-metrics/external/{externalId}
     */
    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get hourly metrics by external ID",
            description = "Retrieve hourly metrics by external ID for cross-service communication"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Metrics not found")
    })
    public ResponseEntity<AnalyticsHourlyMetricsDTO> getMetricsByExternalId(
            @Parameter(description = "External ID", required = true) @PathVariable String externalId) {
        AnalyticsHourlyMetricsDTO metrics = service.getMetricsByExternalId(externalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by specific hour.
     * GET /api/v1/analytics/hourly-metrics/hour/{hour}
     */
    @GetMapping("/hour/{hour}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get hourly metrics by hour",
            description = "Retrieve hourly metrics for a specific hour and company"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Metrics not found")
    })
    public ResponseEntity<AnalyticsHourlyMetricsDTO> getMetricsByHour(
            @Parameter(description = "Hour (ISO 8601 format with timezone)", required = true)
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime hour,
            @Parameter(description = "Company external ID", required = true)
            @RequestParam String companyExternalId) {
        AnalyticsHourlyMetricsDTO metrics = service.getMetricsByHour(hour, companyExternalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics within a time range.
     * GET /api/v1/analytics/hourly-metrics/time-range
     */
    @GetMapping("/time-range")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get hourly metrics by time range",
            description = "Retrieve hourly metrics within a time range. Optional company filter."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid time range"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<AnalyticsHourlyMetricsDTO>> getMetricsByTimeRange(
            @Parameter(description = "Start hour (ISO 8601 format with timezone)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startHour,
            @Parameter(description = "End hour (ISO 8601 format with timezone)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endHour,
            @Parameter(description = "Company external ID (optional, admin only if not provided)")
            @RequestParam(required = false) String companyExternalId) {
        List<AnalyticsHourlyMetricsDTO> metrics = service.getMetricsBetweenHours(
                startHour, endHour, companyExternalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get the latest metrics.
     * GET /api/v1/analytics/hourly-metrics/latest
     */
    @GetMapping("/latest")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get latest hourly metrics",
            description = "Retrieve the most recent hourly metrics. Requires company external ID for non-admin users."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest metrics retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "No metrics found")
    })
    public ResponseEntity<AnalyticsHourlyMetricsDTO> getLatestMetrics(
            @Parameter(description = "Company external ID (optional for admin, required for others)")
            @RequestParam(required = false) String companyExternalId) {
        AnalyticsHourlyMetricsDTO metrics = service.getLatestMetrics(companyExternalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Create new hourly metrics.
     * POST /api/v1/analytics/hourly-metrics
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Create new hourly metrics",
            description = "Creates new hourly analytics metrics. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Metrics created successfully",
                    content = @Content(schema = @Schema(implementation = AnalyticsHourlyMetricsDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "409", description = "Metrics already exist for this hour and company")
    })
    public ResponseEntity<AnalyticsHourlyMetricsDTO> createMetrics(
            @Valid @RequestBody AnalyticsHourlyMetricsDTO dto) {
        AnalyticsHourlyMetricsDTO createdMetrics = service.createMetrics(dto);
        return new ResponseEntity<>(createdMetrics, HttpStatus.CREATED);
    }

    /**
     * Update hourly metrics.
     * PUT /api/v1/analytics/hourly-metrics/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Update hourly metrics",
            description = "Updates existing hourly analytics metrics. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics updated successfully",
                    content = @Content(schema = @Schema(implementation = AnalyticsHourlyMetricsDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Metrics not found")
    })
    public ResponseEntity<AnalyticsHourlyMetricsDTO> updateMetrics(
            @Parameter(description = "Metrics ID", required = true) @PathVariable Long id,
            @Valid @RequestBody AnalyticsHourlyMetricsDTO dto) {
        AnalyticsHourlyMetricsDTO updatedMetrics = service.updateMetrics(id, dto);
        return ResponseEntity.ok(updatedMetrics);
    }

    /**
     * Delete hourly metrics.
     * DELETE /api/v1/analytics/hourly-metrics/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Delete hourly metrics",
            description = "Deletes hourly analytics metrics (soft delete). Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Metrics deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Metrics not found")
    })
    public ResponseEntity<Void> deleteMetrics(
            @Parameter(description = "Metrics ID", required = true) @PathVariable Long id) {
        service.deleteMetrics(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if metrics exist by external ID.
     * GET /api/v1/analytics/hourly-metrics/external/{externalId}/exists
     */
    @GetMapping("/external/{externalId}/exists")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Check if metrics exist by external ID",
            description = "Check if hourly metrics exist by external ID for cross-service validation"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check completed"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Boolean> checkExistsByExternalId(
            @Parameter(description = "External ID", required = true) @PathVariable String externalId) {
        Boolean exists = service.existsByExternalId(externalId);
        return ResponseEntity.ok(exists);
    }
}
