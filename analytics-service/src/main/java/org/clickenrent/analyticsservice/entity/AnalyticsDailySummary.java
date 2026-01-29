package org.clickenrent.analyticsservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.clickenrent.contracts.security.TenantScoped;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing daily aggregated analytics summary.
 * Stores pre-computed metrics for fast query performance.
 * Implements TenantScoped for multi-tenant isolation.
 */
@Entity
@Table(
    name = "analytics_daily_summary",
    indexes = {
        @Index(name = "idx_analytics_daily_summary_date", columnList = "summary_date DESC"),
        @Index(name = "idx_analytics_daily_summary_company", columnList = "company_external_id"),
        @Index(name = "idx_analytics_daily_summary_external_id", columnList = "external_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_analytics_daily_summary_company_date",
            columnNames = {"company_external_id", "summary_date"}
        )
    }
)
@Filter(name = "companyFilter", condition = "company_external_id IN (:companyExternalIds)")
@SQLDelete(sql = "UPDATE analytics_daily_summary SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class AnalyticsDailySummary extends BaseAuditEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100, nullable = false)
    private String externalId;

    @NotNull(message = "Company external ID is required")
    @Column(name = "company_external_id", nullable = false, length = 100)
    private String companyExternalId;

    @NotNull(message = "Summary date is required")
    @Column(name = "summary_date", nullable = false)
    private LocalDate summaryDate;

    // User Metrics
    @Column(name = "new_customers", nullable = false)
    @Builder.Default
    private Integer newCustomers = 0;

    @Column(name = "active_customers", nullable = false)
    @Builder.Default
    private Integer activeCustomers = 0;

    @Column(name = "total_customers", nullable = false)
    @Builder.Default
    private Integer totalCustomers = 0;

    // Bike Rental Metrics
    @Column(name = "total_bike_rentals", nullable = false)
    @Builder.Default
    private Integer totalBikeRentals = 0;

    @Column(name = "completed_bike_rentals", nullable = false)
    @Builder.Default
    private Integer completedBikeRentals = 0;

    @Column(name = "cancelled_bike_rentals", nullable = false)
    @Builder.Default
    private Integer cancelledBikeRentals = 0;

    @Column(name = "total_bike_rental_duration_minutes", nullable = false)
    @Builder.Default
    private Long totalBikeRentalDurationMinutes = 0L;

    @Column(name = "average_bike_rental_duration_minutes", precision = 10, scale = 2)
    private BigDecimal averageBikeRentalDurationMinutes;

    // Revenue Metrics (in cents to avoid floating point issues)
    @Column(name = "total_revenue_cents", nullable = false)
    @Builder.Default
    private Long totalRevenueCents = 0L;

    @Column(name = "total_refunds_cents", nullable = false)
    @Builder.Default
    private Long totalRefundsCents = 0L;

    @Column(name = "average_bike_rental_revenue_cents", precision = 10, scale = 2)
    private BigDecimal averageBikeRentalRevenueCents;

    // Fleet Metrics
    @Column(name = "total_bikes", nullable = false)
    @Builder.Default
    private Integer totalBikes = 0;

    @Column(name = "available_bikes", nullable = false)
    @Builder.Default
    private Integer availableBikes = 0;

    @Column(name = "in_use_bikes", nullable = false)
    @Builder.Default
    private Integer inUseBikes = 0;

    @Column(name = "maintenance_bikes", nullable = false)
    @Builder.Default
    private Integer maintenanceBikes = 0;

    // Location Metrics
    @Column(name = "total_locations", nullable = false)
    @Builder.Default
    private Integer totalLocations = 0;

    @Column(name = "active_locations", nullable = false)
    @Builder.Default
    private Integer activeLocations = 0;

    // Support Metrics
    @Column(name = "new_tickets", nullable = false)
    @Builder.Default
    private Integer newTickets = 0;

    @Column(name = "resolved_tickets", nullable = false)
    @Builder.Default
    private Integer resolvedTickets = 0;

    @Column(name = "open_tickets", nullable = false)
    @Builder.Default
    private Integer openTickets = 0;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }

    @Override
    public String getCompanyExternalId() {
        return this.companyExternalId;
    }
}
