package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

/**
 * Entity representing B2B sale order items.
 * Links orders to any Product subtype (BikeModel, ChargingStationModel, Part, ServiceProduct).
 */
@Entity
@Table(
    name = "b2b_sale_order_item",
    indexes = {
        @Index(name = "idx_b2b_sale_order_item_external_id", columnList = "external_id"),
        @Index(name = "idx_b2b_sale_order_item_order_id", columnList = "b2b_sale_order_id"),
        @Index(name = "idx_b2b_sale_order_item_product_id", columnList = "product_id")
    }
)
@SQLDelete(sql = "UPDATE b2b_sale_order_item SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class B2BSaleOrderItem extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "B2B sale order is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_sale_order_id", nullable = false)
    private B2BSaleOrder b2bSaleOrder;

    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "Quantity is required")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "Price is required")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Total price is required")
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
}

