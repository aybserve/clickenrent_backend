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

/**
 * Entity representing photos attached to bike inspection items.
 * Supports multi-tenant isolation - photos are company-scoped.
 */
@Entity
@Table(
    name = "bike_inspection_item_photo",
    indexes = {
        @Index(name = "idx_bike_inspection_item_photo_item", columnList = "bike_inspection_item_id"),
        @Index(name = "idx_bike_inspection_item_photo_company", columnList = "company_external_id")
    }
)
@SQLDelete(sql = "UPDATE bike_inspection_item_photo SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Filter(name = "companyFilter", condition = "company_external_id IN (:companyExternalIds)")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class BikeInspectionItemPhoto extends BaseAuditEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Bike inspection item is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_inspection_item_id", nullable = false)
    private BikeInspectionItem bikeInspectionItem;

    @Size(max = 500, message = "Photo URL must not exceed 500 characters")
    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @NotNull(message = "Company external ID is required")
    @Size(max = 100, message = "Company external ID must not exceed 100 characters")
    @Column(name = "company_external_id", nullable = false, length = 100)
    private String companyExternalId;

    @Override
    public String getCompanyExternalId() {
        return companyExternalId;
    }
}
