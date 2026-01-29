package org.clickenrent.analyticsservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Data Transfer Object for AnalyticsHourlyMetrics entity.
 * Contains hourly aggregated metrics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsHourlyMetricsDTO {

    private Long id;

    @Pattern(regexp = "^[a-f0-9-]{36}$", message = "Invalid UUID format")
    private String externalId;

    @NotNull(message = "Company external ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9-]{1,100}$", message = "Invalid company external ID format")
    private String companyExternalId;

    @NotNull(message = "Metric hour is required")
    @PastOrPresent(message = "Metric hour cannot be in the future")
    private ZonedDateTime metricHour;

    // Metrics
    @PositiveOrZero(message = "Bike rentals started must be zero or positive")
    private Integer bikeRentalsStarted;

    @PositiveOrZero(message = "Bike rentals completed must be zero or positive")
    private Integer bikeRentalsCompleted;

    @PositiveOrZero(message = "Bike rental revenue must be zero or positive")
    private Long bikeRentalRevenueCents;

    @PositiveOrZero(message = "Active customers must be zero or positive")
    private Integer activeCustomers;

    @PositiveOrZero(message = "New registrations must be zero or positive")
    private Integer newRegistrations;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
