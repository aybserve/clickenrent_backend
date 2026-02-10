package org.clickenrent.analyticsservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
 * Entity representing bike-based analytics metrics by date.
 * Stores pre-computed bike metrics for fast query performance.
 * Implements TenantScoped for multi-tenant isolation.
 */
@Entity
@Table(
    name = "analytics_bike_metrics",
    indexes = {
        @Index(name = "idx_analytics_bike_date", columnList = "metric_date DESC"),
        @Index(name = "idx_analytics_bike_external_id", columnList = "bike_external_id"),
        @Index(name = "idx_analytics_bike_company", columnList = "company_external_id"),
        @Index(name = "idx_analytics_bike_entity_external_id", columnList = "external_id"),
        @Index(name = "idx_analytics_bike_code", columnList = "bike_code")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_analytics_bike_metrics",
            columnNames = {"company_external_id", "metric_date", "bike_external_id"}
        )
    }
)
@Filter(name = "companyFilter", condition = "company_external_id IN (:companyExternalIds)")
@SQLDelete(sql = "UPDATE analytics_bike_metrics SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class AnalyticsBikeMetrics extends BaseAuditEntity implements TenantScoped {

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

    @NotNull(message = "Bike external ID is required")
    @Column(name = "bike_external_id", nullable = false, length = 100)
    private String bikeExternalId;

    @Size(max = 50, message = "Bike code must not exceed 50 characters")
    @Column(name = "bike_code", length = 50)
    private String bikeCode;

    // Metrics
    @Column(name = "total_bike_rentals", nullable = false)
    @Builder.Default
    private Integer totalBikeRentals = 0;

    @Column(name = "total_duration_minutes", nullable = false)
    @Builder.Default
    private Integer totalDurationMinutes = 0;

    @Column(name = "bike_rental_revenue_cents", nullable = false)
    @Builder.Default
    private Long bikeRentalRevenueCents = 0L;

    // Status tracking
    @Column(name = "available_hours", precision = 5, scale = 2)
    private BigDecimal availableHours;

    @Column(name = "in_use_hours", precision = 5, scale = 2)
    private BigDecimal inUseHours;

    @Column(name = "maintenance_hours", precision = 5, scale = 2)
    private BigDecimal maintenanceHours;

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
