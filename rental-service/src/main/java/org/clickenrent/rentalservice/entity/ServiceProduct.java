package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Entity representing a service product (extends Product).
 * Links services to products.
 * Uses JOINED inheritance.
 */
@Entity
@DiscriminatorValue("SERVICE_PRODUCT")
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ServiceProduct extends Product {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = true)
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_product_id", nullable = true)
    private Product relatedProduct;

    @Column(name = "b2b_subscription_price", precision = 10, scale = 2)
    private BigDecimal b2bSubscriptionPrice;
}
