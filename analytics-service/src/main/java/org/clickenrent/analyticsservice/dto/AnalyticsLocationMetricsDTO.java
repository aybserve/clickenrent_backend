package org.clickenrent.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for AnalyticsLocationMetrics entity.
 * Contains location-based metrics aggregated by date.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsLocationMetricsDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Pattern(regexp = "^[a-f0-9-]{36}$", message = "Invalid UUID format")
    private String externalId;

    @NotNull(message = "Company external ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9-]{1,100}$", message = "Invalid company external ID format")
    private String companyExternalId;

    @NotNull(message = "Metric date is required")
    @PastOrPresent(message = "Metric date cannot be in the future")
    private LocalDate metricDate;

    @NotNull(message = "Location external ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9-]{1,100}$", message = "Invalid location external ID format")
    private String locationExternalId;

    // Metrics
    @PositiveOrZero(message = "Total pickups must be zero or positive")
    private Integer totalPickups;

    @PositiveOrZero(message = "Total dropoffs must be zero or positive")
    private Integer totalDropoffs;

    @PositiveOrZero(message = "Unique customers must be zero or positive")
    private Integer uniqueCustomers;

    @PositiveOrZero(message = "Bike rental revenue must be zero or positive")
    private Long bikeRentalRevenueCents;

    @PositiveOrZero(message = "Average bikes available must be zero or positive")
    private BigDecimal averageBikesAvailable;

    // Audit fields
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dateCreated;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastDateModified;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String lastModifiedBy;
}
