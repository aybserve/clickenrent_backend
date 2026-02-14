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
import org.clickenrent.analyticsservice.dto.AnalyticsBikeMetricsDTO;
import org.clickenrent.analyticsservice.service.AnalyticsBikeMetricsService;
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
 * REST controller for Analytics Bike Metrics management operations.
 * Provides endpoints for querying and managing bike-based analytics.
 */
@RestController
@RequestMapping("/api/v1/analytics/bike-metrics")
@RequiredArgsConstructor
@Tag(name = "Analytics Bike Metrics", description = "Bike-based analytics endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsBikeMetricsController {

    private final AnalyticsBikeMetricsService service;

    /**
     * Get all bike metrics with pagination.
     * GET /api/v1/analytics/bike-metrics
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all bike metrics",
            description = "Returns a paginated list of bike analytics metrics. Access control: Admin sees all, B2B sees company metrics."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<AnalyticsBikeMetricsDTO>> getAllMetrics(
            @PageableDefault(size = 20, sort = "metricDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AnalyticsBikeMetricsDTO> metrics = service.getAllMetrics(pageable);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by ID.
     * GET /api/v1/analytics/bike-metrics/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get bike metrics by ID",
            description = "Returns bike analytics metrics by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics found",
                    content = @Content(schema = @Schema(implementation = AnalyticsBikeMetricsDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Metrics not found")
    })
    public ResponseEntity<AnalyticsBikeMetricsDTO> getMetricsById(
            @Parameter(description = "Metrics ID", required = true) @PathVariable Long id) {
        AnalyticsBikeMetricsDTO metrics = service.getMetricsById(id);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by external ID.
     * GET /api/v1/analytics/bike-metrics/external/{externalId}
     */
    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get bike metrics by external ID",
            description = "Retrieve bike metrics by external ID for cross-service communication"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Metrics not found")
    })
    public ResponseEntity<AnalyticsBikeMetricsDTO> getMetricsByExternalId(
            @Parameter(description = "External ID", required = true) @PathVariable String externalId) {
        AnalyticsBikeMetricsDTO metrics = service.getMetricsByExternalId(externalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by date and bike.
     * GET /api/v1/analytics/bike-metrics/date/{date}/bike/{bikeExternalId}
     */
    @GetMapping("/date/{date}/bike/{bikeExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get bike metrics by date and bike",
            description = "Retrieve bike metrics for a specific date and bike"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Metrics not found")
    })
    public ResponseEntity<AnalyticsBikeMetricsDTO> getMetricsByDateAndBike(
            @Parameter(description = "Date (format: yyyy-MM-dd)", required = true)
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Bike external ID", required = true)
            @PathVariable String bikeExternalId,
            @Parameter(description = "Company external ID", required = true)
            @RequestParam String companyExternalId) {
        AnalyticsBikeMetricsDTO metrics = service.getMetricsByDateAndBike(
                date, bikeExternalId, companyExternalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by bike.
     * GET /api/v1/analytics/bike-metrics/bike/{bikeExternalId}
     */
    @GetMapping("/bike/{bikeExternalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get metrics by bike",
            description = "Retrieve all metrics for a specific bike"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<AnalyticsBikeMetricsDTO>> getMetricsByBike(
            @Parameter(description = "Bike external ID", required = true)
            @PathVariable String bikeExternalId,
            @Parameter(description = "Company external ID (optional for admin)")
            @RequestParam(required = false) String companyExternalId) {
        List<AnalyticsBikeMetricsDTO> metrics = service.getMetricsByBike(bikeExternalId, companyExternalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by bike code.
     * GET /api/v1/analytics/bike-metrics/bike-code/{bikeCode}
     */
    @GetMapping("/bike-code/{bikeCode}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get metrics by bike code",
            description = "Retrieve all metrics for bikes with a specific bike code"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<AnalyticsBikeMetricsDTO>> getMetricsByBikeCode(
            @Parameter(description = "Bike code", required = true)
            @PathVariable String bikeCode,
            @Parameter(description = "Company external ID (optional for admin)")
            @RequestParam(required = false) String companyExternalId) {
        List<AnalyticsBikeMetricsDTO> metrics = service.getMetricsByBikeCode(bikeCode, companyExternalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics by date.
     * GET /api/v1/analytics/bike-metrics/date/{date}
     */
    @GetMapping("/date/{date}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get metrics by date",
            description = "Retrieve all bike metrics for a specific date"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<AnalyticsBikeMetricsDTO>> getMetricsByDate(
            @Parameter(description = "Date (format: yyyy-MM-dd)", required = true)
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Company external ID (optional for admin)")
            @RequestParam(required = false) String companyExternalId) {
        List<AnalyticsBikeMetricsDTO> metrics = service.getMetricsByDate(date, companyExternalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get metrics within a date range.
     * GET /api/v1/analytics/bike-metrics/date-range
     */
    @GetMapping("/date-range")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get bike metrics by date range",
            description = "Retrieve bike metrics within a date range. Optional bike and company filters."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<AnalyticsBikeMetricsDTO>> getMetricsByDateRange(
            @Parameter(description = "Start date (format: yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (format: yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Bike external ID (optional)")
            @RequestParam(required = false) String bikeExternalId,
            @Parameter(description = "Company external ID (optional, admin only if not provided)")
            @RequestParam(required = false) String companyExternalId) {
        List<AnalyticsBikeMetricsDTO> metrics = service.getMetricsBetweenDates(
                startDate, endDate, bikeExternalId, companyExternalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get the latest metrics for a bike.
     * GET /api/v1/analytics/bike-metrics/bike/{bikeExternalId}/latest
     */
    @GetMapping("/bike/{bikeExternalId}/latest")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get latest bike metrics",
            description = "Retrieve the most recent metrics for a specific bike"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest metrics retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "No metrics found")
    })
    public ResponseEntity<AnalyticsBikeMetricsDTO> getLatestMetrics(
            @Parameter(description = "Bike external ID", required = true)
            @PathVariable String bikeExternalId,
            @Parameter(description = "Company external ID (optional for admin)")
            @RequestParam(required = false) String companyExternalId) {
        AnalyticsBikeMetricsDTO metrics = service.getLatestMetrics(bikeExternalId, companyExternalId);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Create new bike metrics.
     * POST /api/v1/analytics/bike-metrics
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Create new bike metrics",
            description = "Creates new bike analytics metrics. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Metrics created successfully",
                    content = @Content(schema = @Schema(implementation = AnalyticsBikeMetricsDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "409", description = "Metrics already exist for this date, bike, and company")
    })
    public ResponseEntity<AnalyticsBikeMetricsDTO> createMetrics(
            @Valid @RequestBody AnalyticsBikeMetricsDTO dto) {
        AnalyticsBikeMetricsDTO createdMetrics = service.createMetrics(dto);
        return new ResponseEntity<>(createdMetrics, HttpStatus.CREATED);
    }

    /**
     * Update bike metrics.
     * PUT /api/v1/analytics/bike-metrics/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Update bike metrics",
            description = "Updates existing bike analytics metrics. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics updated successfully",
                    content = @Content(schema = @Schema(implementation = AnalyticsBikeMetricsDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Metrics not found")
    })
    public ResponseEntity<AnalyticsBikeMetricsDTO> updateMetrics(
            @Parameter(description = "Metrics ID", required = true) @PathVariable Long id,
            @Valid @RequestBody AnalyticsBikeMetricsDTO dto) {
        AnalyticsBikeMetricsDTO updatedMetrics = service.updateMetrics(id, dto);
        return ResponseEntity.ok(updatedMetrics);
    }

    /**
     * Delete bike metrics.
     * DELETE /api/v1/analytics/bike-metrics/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Delete bike metrics",
            description = "Deletes bike analytics metrics (soft delete). Requires SUPERADMIN or ADMIN role."
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
     * GET /api/v1/analytics/bike-metrics/external/{externalId}/exists
     */
    @GetMapping("/external/{externalId}/exists")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Check if metrics exist by external ID",
            description = "Check if bike metrics exist by external ID for cross-service validation"
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
