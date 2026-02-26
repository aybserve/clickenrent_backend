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
import org.clickenrent.analyticsservice.dto.AnalyticsLocationMetricsDTO;
import org.clickenrent.analyticsservice.service.AnalyticsLocationMetricsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for Analytics Location Metrics management operations.
 * Provides endpoints for querying and managing location-based analytics.
 */
@RestController
@RequestMapping("/api/v1/analytics/location-metrics")
@RequiredArgsConstructor
@Tag(name = "Analytics Location Metrics", description = "Location-based analytics endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsLocationMetricsController {

    private final AnalyticsLocationMetricsService service;

    /**
     * Get all location metrics with pagination.
     * GET /api/v1/analytics/location-metrics
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all location metrics",
            description = "Returns a paginated list of location analytics metrics. Access control: Admin sees all, B2B sees company metrics."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<AnalyticsLocationMetricsDTO>> getAllMetrics(
            @PageableDefault(size = 20, sort = "metricDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AnalyticsLocationMetricsDTO> metrics = service.getAllMetrics(pageable);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by ID.
     * GET /api/v1/analytics/location-metrics/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get location metrics by ID",
            description = "Returns location analytics metrics by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics found",
                    content = @Content(schema = @Schema(implementation = AnalyticsLocationMetricsDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Metrics not found")
    })
    public ResponseEntity<AnalyticsLocationMetricsDTO> getMetricsById(
            @Parameter(description = "Metrics ID", required = true) @PathVariable Long id) {
        AnalyticsLocationMetricsDTO metrics = service.getMetricsById(id);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by external ID.
     * GET /api/v1/analytics/location-metrics/external/{externalId}
     */
    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get location metrics by external ID",
            description = "Retrieve location metrics by external ID for cross-service communication"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Metrics not found")
    })
    public ResponseEntity<AnalyticsLocationMetricsDTO> getMetricsByExternalId(
            @Parameter(description = "External ID", required = true) @PathVariable String externalId) {
        AnalyticsLocationMetricsDTO metrics = service.getMetricsByExternalId(externalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by date and location.
     * GET /api/v1/analytics/location-metrics/date/{date}/location/{locationExternalId}
     */
    @GetMapping("/date/{date}/location/{locationExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get location metrics by date and location",
            description = "Retrieve location metrics for a specific date and location"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Metrics not found")
    })
    public ResponseEntity<AnalyticsLocationMetricsDTO> getMetricsByDateAndLocation(
            @Parameter(description = "Date (format: yyyy-MM-dd)", required = true)
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Location external ID", required = true)
            @PathVariable String locationExternalId,
            @Parameter(description = "Company external ID", required = true)
            @RequestParam String companyExternalId) {
        AnalyticsLocationMetricsDTO metrics = service.getMetricsByDateAndLocation(
                date, locationExternalId, companyExternalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by location.
     * GET /api/v1/analytics/location-metrics/location/{locationExternalId}
     */
    @GetMapping("/location/{locationExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get metrics by location",
            description = "Retrieve all metrics for a specific location"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<AnalyticsLocationMetricsDTO>> getMetricsByLocation(
            @Parameter(description = "Location external ID", required = true)
            @PathVariable String locationExternalId,
            @Parameter(description = "Company external ID (optional for admin)")
            @RequestParam(required = false) String companyExternalId) {
        List<AnalyticsLocationMetricsDTO> metrics = service.getMetricsByLocation(
                locationExternalId, companyExternalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by date.
     * GET /api/v1/analytics/location-metrics/date/{date}
     */
    @GetMapping("/date/{date}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get metrics by date",
            description = "Retrieve all location metrics for a specific date"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<AnalyticsLocationMetricsDTO>> getMetricsByDate(
            @Parameter(description = "Date (format: yyyy-MM-dd)", required = true)
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Company external ID (optional for admin)")
            @RequestParam(required = false) String companyExternalId) {
        List<AnalyticsLocationMetricsDTO> metrics = service.getMetricsByDate(date, companyExternalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics within a date range.
     * GET /api/v1/analytics/location-metrics/date-range
     */
    @GetMapping("/date-range")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get location metrics by date range",
            description = "Retrieve location metrics within a date range. Optional location and company filters."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<AnalyticsLocationMetricsDTO>> getMetricsByDateRange(
            @Parameter(description = "Start date (format: yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (format: yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Location external ID (optional)")
            @RequestParam(required = false) String locationExternalId,
            @Parameter(description = "Company external ID (optional, admin only if not provided)")
            @RequestParam(required = false) String companyExternalId) {
        List<AnalyticsLocationMetricsDTO> metrics = service.getMetricsBetweenDates(
                startDate, endDate, locationExternalId, companyExternalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get the latest metrics for a location.
     * GET /api/v1/analytics/location-metrics/location/{locationExternalId}/latest
     */
    @GetMapping("/location/{locationExternalId}/latest")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get latest location metrics",
            description = "Retrieve the most recent metrics for a specific location"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest metrics retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "No metrics found")
    })
    public ResponseEntity<AnalyticsLocationMetricsDTO> getLatestMetrics(
            @Parameter(description = "Location external ID", required = true)
            @PathVariable String locationExternalId,
            @Parameter(description = "Company external ID (optional for admin)")
            @RequestParam(required = false) String companyExternalId) {
        AnalyticsLocationMetricsDTO metrics = service.getLatestMetrics(locationExternalId, companyExternalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Create new location metrics.
     * POST /api/v1/analytics/location-metrics
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Create new location metrics",
            description = "Creates new location analytics metrics. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Metrics created successfully",
                    content = @Content(schema = @Schema(implementation = AnalyticsLocationMetricsDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "409", description = "Metrics already exist for this date, location, and company")
    })
    public ResponseEntity<AnalyticsLocationMetricsDTO> createMetrics(
            @Valid @RequestBody AnalyticsLocationMetricsDTO dto) {
        AnalyticsLocationMetricsDTO createdMetrics = service.createMetrics(dto);
        return new ResponseEntity<>(createdMetrics, HttpStatus.CREATED);
    }

    /**
     * Update location metrics.
     * PUT /api/v1/analytics/location-metrics/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Update location metrics",
            description = "Updates existing location analytics metrics. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics updated successfully",
                    content = @Content(schema = @Schema(implementation = AnalyticsLocationMetricsDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Metrics not found")
    })
    public ResponseEntity<AnalyticsLocationMetricsDTO> updateMetrics(
            @Parameter(description = "Metrics ID", required = true) @PathVariable Long id,
            @Valid @RequestBody AnalyticsLocationMetricsDTO dto) {
        AnalyticsLocationMetricsDTO updatedMetrics = service.updateMetrics(id, dto);
        return ResponseEntity.ok(updatedMetrics);
    }

    /**
     * Delete location metrics.
     * DELETE /api/v1/analytics/location-metrics/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Delete location metrics",
            description = "Deletes location analytics metrics (soft delete). Requires SUPERADMIN or ADMIN role."
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
     * GET /api/v1/analytics/location-metrics/external/{externalId}/exists
     */
    @GetMapping("/external/{externalId}/exists")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Check if metrics exist by external ID",
            description = "Check if location metrics exist by external ID for cross-service validation"
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
