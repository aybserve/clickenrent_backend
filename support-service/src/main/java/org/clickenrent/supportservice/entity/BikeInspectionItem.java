package org.clickenrent.supportservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.clickenrent.contracts.security.TenantScoped;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

/**
 * Entity representing individual items within a bike inspection.
 * Supports multi-tenant isolation - bike inspection items are company-scoped.
 */
@Entity
@Table(
    name = "bike_inspection_item",
    indexes = {
        @Index(name = "idx_bike_inspection_item_external_id", columnList = "external_id"),
        @Index(name = "idx_bike_inspection_item_inspection", columnList = "bike_inspection_id"),
        @Index(name = "idx_bike_inspection_item_bike_external_id", columnList = "bike_external_id"),
        @Index(name = "idx_bike_inspection_item_company", columnList = "company_external_id")
    }
)
@SQLDelete(sql = "UPDATE bike_inspection_item SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Filter(name = "companyFilter", condition = "company_external_id IN (:companyExternalIds)")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class BikeInspectionItem extends BaseAuditEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "Bike inspection is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_inspection_id", nullable = false)
    private BikeInspection bikeInspection;

    @Column(name = "bike_external_id", length = 100)
    private String bikeExternalId;

    @NotNull(message = "Company external ID is required")
    @Size(max = 100, message = "Company external ID must not exceed 100 characters")
    @Column(name = "company_external_id", nullable = false, length = 100)
    private String companyExternalId;

    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    @Column(name = "comment", length = 2000)
    private String comment;

    @NotNull(message = "Bike inspection item status is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_inspection_item_status_id", nullable = false)
    private BikeInspectionItemStatus bikeInspectionItemStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_code_id")
    private ErrorCode errorCode;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }

    @Override
    public String getCompanyExternalId() {
        return companyExternalId;
    }
}
