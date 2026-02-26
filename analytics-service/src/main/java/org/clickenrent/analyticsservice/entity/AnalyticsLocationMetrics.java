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
 * Entity representing location-based analytics metrics by date.
 * Stores pre-computed location metrics for fast query performance.
 * Implements TenantScoped for multi-tenant isolation.
 */
@Entity
@Table(
    name = "analytics_location_metrics",
    indexes = {
        @Index(name = "idx_analytics_location_date", columnList = "metric_date DESC"),
        @Index(name = "idx_analytics_location_external_id", columnList = "location_external_id"),
        @Index(name = "idx_analytics_location_company", columnList = "company_external_id"),
        @Index(name = "idx_analytics_location_entity_external_id", columnList = "external_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_analytics_location_metrics",
            columnNames = {"company_external_id", "metric_date", "location_external_id"}
        )
    }
)
@Filter(name = "companyFilter", condition = "company_external_id IN (:companyExternalIds)")
@SQLDelete(sql = "UPDATE analytics_location_metrics SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class AnalyticsLocationMetrics extends BaseAuditEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100, nullable = false)
    private String externalId;

    @NotNull(message = "Company external ID is required")
    @Column(name = "company_external_id", nullable = false, length = 100)
    private String companyExternalId;

    @NotNull(message = "Metric date is required")
    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @NotNull(message = "Location external ID is required")
    @Column(name = "location_external_id", nullable = false, length = 100)
    private String locationExternalId;

    // Metrics
    @Column(name = "total_pickups", nullable = false)
    @Builder.Default
    private Integer totalPickups = 0;

    @Column(name = "total_dropoffs", nullable = false)
    @Builder.Default
    private Integer totalDropoffs = 0;

    @Column(name = "unique_customers", nullable = false)
    @Builder.Default
    private Integer uniqueCustomers = 0;

    @Column(name = "bike_rental_revenue_cents", nullable = false)
    @Builder.Default
    private Long bikeRentalRevenueCents = 0L;

    @Column(name = "average_bikes_available", precision = 5, scale = 2)
    private BigDecimal averageBikesAvailable;

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
