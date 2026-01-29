package org.clickenrent.analyticsservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for AnalyticsBikeMetrics entity.
 * Contains bike-based metrics aggregated by date.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsBikeMetricsDTO {

    private Long id;

    @Pattern(regexp = "^[a-f0-9-]{36}$", message = "Invalid UUID format")
    private String externalId;

    @NotNull(message = "Company external ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9-]{1,100}$", message = "Invalid company external ID format")
    private String companyExternalId;

    @NotNull(message = "Metric date is required")
    @PastOrPresent(message = "Metric date cannot be in the future")
    private LocalDate metricDate;

    @NotNull(message = "Bike external ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9-]{1,100}$", message = "Invalid bike external ID format")
    private String bikeExternalId;

    @Size(max = 50, message = "Bike code must not exceed 50 characters")
    private String bikeCode;

    // Metrics
    @PositiveOrZero(message = "Total bike rentals must be zero or positive")
    private Integer totalBikeRentals;

    @PositiveOrZero(message = "Total duration minutes must be zero or positive")
    private Integer totalDurationMinutes;

    @PositiveOrZero(message = "Bike rental revenue must be zero or positive")
    private Long bikeRentalRevenueCents;

    // Status tracking
    @PositiveOrZero(message = "Available hours must be zero or positive")
    private BigDecimal availableHours;

    @PositiveOrZero(message = "In-use hours must be zero or positive")
    private BigDecimal inUseHours;

    @PositiveOrZero(message = "Maintenance hours must be zero or positive")
    private BigDecimal maintenanceHours;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
