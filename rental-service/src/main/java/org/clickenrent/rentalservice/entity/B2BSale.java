package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.clickenrent.contracts.security.TenantScoped;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

/**
 * Entity representing B2B sales between companies.
 * Implements TenantScoped for multi-tenant isolation.
 * Note: B2BSale has TWO company filters (seller and buyer).
 */
@Entity
@Table(
    name = "b2b_sale",
    indexes = {
        @Index(name = "idx_b2b_sale_external_id", columnList = "external_id"),
        @Index(name = "idx_b2b_sale_location_id", columnList = "location_id"),
        @Index(name = "idx_b2b_sale_seller_company_external_id", columnList = "seller_company_external_id"),
        @Index(name = "idx_b2b_sale_buyer_company_external_id", columnList = "buyer_company_external_id")
    }
)
@Filter(name = "sellerCompanyFilter", condition = "seller_company_external_id IN (:companyExternalIds)")
@Filter(name = "buyerCompanyFilter", condition = "buyer_company_external_id IN (:companyExternalIds)")
@SQLDelete(sql = "UPDATE b2b_sale SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class B2BSale extends BaseAuditEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "Location is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @NotNull(message = "B2B sale status is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_sale_status_id", nullable = false)
    private B2BSaleStatus b2bSaleStatus;

    @NotNull(message = "Seller company external ID is required")
    @Column(name = "seller_company_external_id", nullable = false, length = 100)
    private String sellerCompanyExternalId;

    @NotNull(message = "Buyer company external ID is required")
    @Column(name = "buyer_company_external_id", nullable = false, length = 100)
    private String buyerCompanyExternalId;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }
    
    @Override
    public String getCompanyExternalId() {
        // For B2BSale, return seller company ID as primary tenant
        return this.sellerCompanyExternalId;
    }
}
