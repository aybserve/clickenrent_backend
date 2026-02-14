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
import org.clickenrent.analyticsservice.dto.AnalyticsDailySummaryDTO;
import org.clickenrent.analyticsservice.service.AnalyticsDailySummaryService;
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
 * REST controller for Analytics Daily Summary management operations.
 * Provides endpoints for querying and managing aggregated daily analytics.
 */
@RestController
@RequestMapping("/api/v1/analytics/daily-summaries")
@RequiredArgsConstructor
@Tag(name = "Analytics Daily Summary", description = "Daily aggregated analytics endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsDailySummaryController {

    private final AnalyticsDailySummaryService service;

    /**
     * Get all daily summaries with pagination.
     * GET /api/v1/analytics/daily-summaries
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all daily summaries",
            description = "Returns a paginated list of daily analytics summaries. Access control: Admin sees all, B2B sees company summaries."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summaries retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public ResponseEntity<Page<AnalyticsDailySummaryDTO>> getAllSummaries(
            @PageableDefault(size = 20, sort = "summaryDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AnalyticsDailySummaryDTO> summaries = service.getAllSummaries(pageable);
        return ResponseEntity.ok(summaries);
    }

    /**
     * Get summary by ID.
     * GET /api/v1/analytics/daily-summaries/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get daily summary by ID",
            description = "Returns daily analytics summary by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary found",
                    content = @Content(schema = @Schema(implementation = AnalyticsDailySummaryDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Summary not found")
    })
    public ResponseEntity<AnalyticsDailySummaryDTO> getSummaryById(
            @Parameter(description = "Summary ID", required = true) @PathVariable Long id) {
        AnalyticsDailySummaryDTO summary = service.getSummaryById(id);
        return ResponseEntity.ok(summary);
    }

    /**
     * Get summary by external ID.
     * GET /api/v1/analytics/daily-summaries/external/{externalId}
     */
    @GetMapping("/external/{externalId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get daily summary by external ID",
            description = "Retrieve daily summary by external ID for cross-service communication"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Summary not found")
    })
    public ResponseEntity<AnalyticsDailySummaryDTO> getSummaryByExternalId(
            @Parameter(description = "External ID", required = true) @PathVariable String externalId) {
        AnalyticsDailySummaryDTO summary = service.getSummaryByExternalId(externalId);
        return ResponseEntity.ok(summary);
    }

    /**
     * Get summary by specific date.
     * GET /api/v1/analytics/daily-summaries/date/{date}
     */
    @GetMapping("/date/{date}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get daily summary by date",
            description = "Retrieve daily summary for a specific date and company"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Summary not found")
    })
    public ResponseEntity<AnalyticsDailySummaryDTO> getSummaryByDate(
            @Parameter(description = "Date (format: yyyy-MM-dd)", required = true)
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Company external ID", required = true)
            @RequestParam String companyExternalId) {
        AnalyticsDailySummaryDTO summary = service.getSummaryByDate(date, companyExternalId);
        return ResponseEntity.ok(summary);
    }

    /**
     * Get summaries within a date range.
     * GET /api/v1/analytics/daily-summaries/date-range
     */
    @GetMapping("/date-range")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get daily summaries by date range",
            description = "Retrieve daily summaries within a date range. Optional company filter."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summaries retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<AnalyticsDailySummaryDTO>> getSummariesByDateRange(
            @Parameter(description = "Start date (format: yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (format: yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Company external ID (optional, admin only if not provided)")
            @RequestParam(required = false) String companyExternalId) {
        List<AnalyticsDailySummaryDTO> summaries = service.getSummariesBetweenDates(
                startDate, endDate, companyExternalId);
        return ResponseEntity.ok(summaries);
    }

    /**
     * Get the latest summary.
     * GET /api/v1/analytics/daily-summaries/latest
     */
    @GetMapping("/latest")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get latest daily summary",
            description = "Retrieve the most recent daily summary. Requires company external ID for non-admin users."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest summary retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "No summaries found")
    })
    public ResponseEntity<AnalyticsDailySummaryDTO> getLatestSummary(
            @Parameter(description = "Company external ID (optional for admin, required for others)")
            @RequestParam(required = false) String companyExternalId) {
        AnalyticsDailySummaryDTO summary = service.getLatestSummary(companyExternalId);
        return ResponseEntity.ok(summary);
    }

    /**
     * Create a new daily summary.
     * POST /api/v1/analytics/daily-summaries
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Create a new daily summary",
            description = "Creates a new daily analytics summary. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Summary created successfully",
                    content = @Content(schema = @Schema(implementation = AnalyticsDailySummaryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "409", description = "Summary already exists for this date and company")
    })
    public ResponseEntity<AnalyticsDailySummaryDTO> createSummary(
            @Valid @RequestBody AnalyticsDailySummaryDTO dto) {
        AnalyticsDailySummaryDTO createdSummary = service.createSummary(dto);
        return new ResponseEntity<>(createdSummary, HttpStatus.CREATED);
    }

    /**
     * Update a daily summary.
     * PUT /api/v1/analytics/daily-summaries/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Update daily summary",
            description = "Updates an existing daily analytics summary. Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary updated successfully",
                    content = @Content(schema = @Schema(implementation = AnalyticsDailySummaryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Summary not found")
    })
    public ResponseEntity<AnalyticsDailySummaryDTO> updateSummary(
            @Parameter(description = "Summary ID", required = true) @PathVariable Long id,
            @Valid @RequestBody AnalyticsDailySummaryDTO dto) {
        AnalyticsDailySummaryDTO updatedSummary = service.updateSummary(id, dto);
        return ResponseEntity.ok(updatedSummary);
    }

    /**
     * Delete a daily summary.
     * DELETE /api/v1/analytics/daily-summaries/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    @Operation(
            summary = "Delete daily summary",
            description = "Deletes a daily analytics summary (soft delete). Requires SUPERADMIN or ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Summary deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Summary not found")
    })
    public ResponseEntity<Void> deleteSummary(
            @Parameter(description = "Summary ID", required = true) @PathVariable Long id) {
        service.deleteSummary(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if summary exists by external ID.
     * GET /api/v1/analytics/daily-summaries/external/{externalId}/exists
     */
    @GetMapping("/external/{externalId}/exists")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Check if summary exists by external ID",
            description = "Check if a daily summary exists by external ID for cross-service validation"
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
