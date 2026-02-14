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
 * Data Transfer Object for AnalyticsDailySummary entity.
 * Contains aggregated daily metrics across all domains.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDailySummaryDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Pattern(regexp = "^[a-f0-9-]{36}$", message = "Invalid UUID format")
    private String externalId;

    @NotNull(message = "Company external ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9-]{1,100}$", message = "Invalid company external ID format")
    private String companyExternalId;

    @NotNull(message = "Summary date is required")
    @PastOrPresent(message = "Summary date cannot be in the future")
    private LocalDate summaryDate;

    // User Metrics
    @PositiveOrZero(message = "New customers must be zero or positive")
    private Integer newCustomers;

    @PositiveOrZero(message = "Active customers must be zero or positive")
    private Integer activeCustomers;

    @PositiveOrZero(message = "Total customers must be zero or positive")
    private Integer totalCustomers;

    // Bike Rental Metrics
    @PositiveOrZero(message = "Total bike rentals must be zero or positive")
    private Integer totalBikeRentals;

    @PositiveOrZero(message = "Completed bike rentals must be zero or positive")
    private Integer completedBikeRentals;

    @PositiveOrZero(message = "Cancelled bike rentals must be zero or positive")
    private Integer cancelledBikeRentals;

    @PositiveOrZero(message = "Total bike rental duration must be zero or positive")
    private Long totalBikeRentalDurationMinutes;

    @PositiveOrZero(message = "Average bike rental duration must be zero or positive")
    private BigDecimal averageBikeRentalDurationMinutes;

    // Revenue Metrics (in cents)
    @PositiveOrZero(message = "Total revenue must be zero or positive")
    private Long totalRevenueCents;

    @PositiveOrZero(message = "Total refunds must be zero or positive")
    private Long totalRefundsCents;

    @PositiveOrZero(message = "Average bike rental revenue must be zero or positive")
    private BigDecimal averageBikeRentalRevenueCents;

    // Fleet Metrics
    @PositiveOrZero(message = "Total bikes must be zero or positive")
    private Integer totalBikes;

    @PositiveOrZero(message = "Available bikes must be zero or positive")
    private Integer availableBikes;

    @PositiveOrZero(message = "In-use bikes must be zero or positive")
    private Integer inUseBikes;

    @PositiveOrZero(message = "Maintenance bikes must be zero or positive")
    private Integer maintenanceBikes;

    // Location Metrics
    @PositiveOrZero(message = "Total locations must be zero or positive")
    private Integer totalLocations;

    @PositiveOrZero(message = "Active locations must be zero or positive")
    private Integer activeLocations;

    // Support Metrics
    @PositiveOrZero(message = "New tickets must be zero or positive")
    private Integer newTickets;

    @PositiveOrZero(message = "Resolved tickets must be zero or positive")
    private Integer resolvedTickets;

    @PositiveOrZero(message = "Open tickets must be zero or positive")
    private Integer openTickets;

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
