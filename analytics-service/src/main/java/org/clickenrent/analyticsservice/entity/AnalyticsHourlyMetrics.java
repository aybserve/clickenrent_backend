package org.clickenrent.analyticsservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.clickenrent.contracts.security.TenantScoped;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Entity representing hourly aggregated analytics metrics.
 * Stores pre-computed hourly metrics for fast query performance.
 * Implements TenantScoped for multi-tenant isolation.
 */
@Entity
@Table(
    name = "analytics_hourly_metrics",
    indexes = {
        @Index(name = "idx_analytics_hourly_hour", columnList = "metric_hour DESC"),
        @Index(name = "idx_analytics_hourly_company", columnList = "company_external_id"),
        @Index(name = "idx_analytics_hourly_external_id", columnList = "external_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_analytics_hourly_metrics_company_hour",
            columnNames = {"company_external_id", "metric_hour"}
        )
    }
)
@Filter(name = "companyFilter", condition = "company_external_id IN (:companyExternalIds)")
@SQLDelete(sql = "UPDATE analytics_hourly_metrics SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class AnalyticsHourlyMetrics extends BaseAuditEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100, nullable = false)
    private String externalId;

    @NotNull(message = "Company external ID is required")
    @Column(name = "company_external_id", nullable = false, length = 100)
    private String companyExternalId;

    @NotNull(message = "Metric hour is required")
    @Column(name = "metric_hour", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime metricHour;

    // Metrics
    @Column(name = "bike_rentals_started", nullable = false)
    @Builder.Default
    private Integer bikeRentalsStarted = 0;

    @Column(name = "bike_rentals_completed", nullable = false)
    @Builder.Default
    private Integer bikeRentalsCompleted = 0;

    @Column(name = "bike_rental_revenue_cents", nullable = false)
    @Builder.Default
    private Long bikeRentalRevenueCents = 0L;

    @Column(name = "active_customers", nullable = false)
    @Builder.Default
    private Integer activeCustomers = 0;

    @Column(name = "new_registrations", nullable = false)
    @Builder.Default
    private Integer newRegistrations = 0;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getExternalId() {
        return this.externalId;
    }

    @Override
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

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
