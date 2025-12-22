package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

/**
 * Entity representing B2B subscription order items.
 * Links subscription orders to product model types (BikeModel, BikeType, Part, ServiceProduct).
 */
@Entity
@Table(
    name = "b2b_subscription_order_item",
    indexes = {
        @Index(name = "idx_b2b_subscription_order_item_external_id", columnList = "external_id"),
        @Index(name = "idx_b2b_subscription_order_item_order_id", columnList = "b2b_subscription_order_id")
    }
)
@SQLDelete(sql = "UPDATE b2b_subscription_order_item SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class B2BSubscriptionOrderItem extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "B2B subscription order is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_subscription_order_id", nullable = false)
    private B2BSubscriptionOrder b2bSubscriptionOrder;

    @NotNull(message = "Product model type is required")
    @Column(name = "product_model_type", nullable = false, length = 50)
    private String productModelType;

    @NotNull(message = "Product model ID is required")
    @Column(name = "product_model_id", nullable = false)
    private Long productModelId;

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




