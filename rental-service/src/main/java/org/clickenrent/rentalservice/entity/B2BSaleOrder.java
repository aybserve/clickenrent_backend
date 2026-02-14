package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.clickenrent.contracts.security.TenantScoped;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Entity representing B2B sale orders between companies.
 * Orders that eventually create a B2BSale when completed/processed.
 * Implements TenantScoped for multi-tenant isolation.
 * Note: B2BSaleOrder has TWO company filters (seller and buyer).
 */
@Entity
@Table(
    name = "b2b_sale_order",
    indexes = {
        @Index(name = "idx_b2b_sale_order_external_id", columnList = "external_id"),
        @Index(name = "idx_b2b_sale_order_seller_company_external_id", columnList = "seller_company_external_id"),
        @Index(name = "idx_b2b_sale_order_buyer_company_external_id", columnList = "buyer_company_external_id")
    }
)
@Filter(name = "sellerCompanyFilter", condition = "seller_company_external_id IN (:companyExternalIds)")
@Filter(name = "buyerCompanyFilter", condition = "buyer_company_external_id IN (:companyExternalIds)")
@SQLDelete(sql = "UPDATE b2b_sale_order SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class B2BSaleOrder extends BaseAuditEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "Seller company external ID is required")
    @Column(name = "seller_company_external_id", nullable = false, length = 100)
    private String sellerCompanyExternalId;

    @NotNull(message = "Buyer company external ID is required")
    @Column(name = "buyer_company_external_id", nullable = false, length = 100)
    private String buyerCompanyExternalId;

    @NotNull(message = "B2B sale order status is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_sale_order_status_id", nullable = false)
    private B2BSaleOrderStatus b2bSaleOrderStatus;

    @NotNull(message = "Location is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_sale_id")
    private B2BSale b2bSale;
    
    @Override
    public String getCompanyExternalId() {
        // For B2BSaleOrder, return seller company ID as primary tenant
        return this.sellerCompanyExternalId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getExternalId() {
        return externalId;
    }

    @Override
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}




