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
 * Junction entity linking bike inspection items to bike issues.
 * Supports multi-tenant isolation - links are company-scoped.
 */
@Entity
@Table(
    name = "bike_inspection_item_bike_issue",
    indexes = {
        @Index(name = "idx_bike_inspection_item_bike_issue_external_id", columnList = "external_id"),
        @Index(name = "idx_bike_inspection_item_bike_issue_item", columnList = "bike_inspection_item_id"),
        @Index(name = "idx_bike_inspection_item_bike_issue_issue", columnList = "bike_issue_id"),
        @Index(name = "idx_bike_inspection_item_bike_issue_company", columnList = "company_external_id")
    }
)
@SQLDelete(sql = "UPDATE bike_inspection_item_bike_issue SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Filter(name = "companyFilter", condition = "company_external_id IN (:companyExternalIds)")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
public class BikeInspectionItemBikeIssue extends BaseAuditEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", length = 100, unique = true)
    private String externalId;

    @NotNull(message = "Bike inspection item is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_inspection_item_id", nullable = false)
    private BikeInspectionItem bikeInspectionItem;

    @NotNull(message = "Bike issue is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_issue_id", nullable = false)
    private BikeIssue bikeIssue;

    @NotNull(message = "Company external ID is required")
    @Size(max = 100, message = "Company external ID must not exceed 100 characters")
    @Column(name = "company_external_id", nullable = false, length = 100)
    private String companyExternalId;

    @Override
    public String getCompanyExternalId() {
        return companyExternalId;
    }
}
