package org.clickenrent.supportservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.clickenrent.contracts.security.TenantScoped;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Junction entity linking bike inspection items to bike units with problem tracking.
 * Supports multi-tenant isolation - links are company-scoped.
 */
@Entity
@Table(
    name = "bike_inspection_item_bike_unit",
    indexes = {
        @Index(name = "idx_bike_inspection_item_bike_unit_external_id", columnList = "external_id"),
        @Index(name = "idx_bike_inspection_item_bike_unit_item", columnList = "bike_inspection_item_id"),
        @Index(name = "idx_bike_inspection_item_bike_unit_unit", columnList = "bike_unit_id"),
        @Index(name = "idx_bike_inspection_item_bike_unit_company", columnList = "company_external_id")
    }
)
@SQLDelete(sql = "UPDATE bike_inspection_item_bike_unit SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Filter(name = "companyFilter", condition = "company_external_id IN (:companyExternalIds)")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
public class BikeInspectionItemBikeUnit extends BaseAuditEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", length = 100, unique = true)
    private String externalId;

    @NotNull(message = "Bike inspection item is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_inspection_item_id", nullable = false)
    private BikeInspectionItem bikeInspectionItem;

    @NotNull(message = "Bike unit is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_unit_id", nullable = false)
    private BikeUnit bikeUnit;

    @Builder.Default
    @Column(name = "has_problem", nullable = false)
    private Boolean hasProblem = false;

    @NotNull(message = "Company external ID is required")
    @Size(max = 100, message = "Company external ID must not exceed 100 characters")
    @Column(name = "company_external_id", nullable = false, length = 100)
    private String companyExternalId;

    @Override
    public String getCompanyExternalId() {
        return companyExternalId;
    }
}
