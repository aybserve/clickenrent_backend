package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Entity representing B2B sale orders between companies.
 * Orders that eventually create a B2BSale when completed/processed.
 */
@Entity
@Table(
    name = "b2b_sale_order",
    indexes = {
        @Index(name = "idx_b2b_sale_order_external_id", columnList = "external_id"),
        @Index(name = "idx_b2b_sale_order_seller_company_id", columnList = "seller_company_id"),
        @Index(name = "idx_b2b_sale_order_buyer_company_id", columnList = "buyer_company_id")
    }
)
@SQLDelete(sql = "UPDATE b2b_sale_order SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class B2BSaleOrder extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "Seller company ID is required")
    @Column(name = "seller_company_id", nullable = false)
    private Long sellerCompanyId;

    @NotNull(message = "Buyer company ID is required")
    @Column(name = "buyer_company_id", nullable = false)
    private Long buyerCompanyId;

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

    @NotNull(message = "Date time is required")
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;
}




