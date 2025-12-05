package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing a service product (extends Product).
 * Links services to products.
 * Uses SINGLE_TABLE inheritance.
 */
@Entity
@DiscriminatorValue("SERVICE_PRODUCT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ServiceProduct extends Product {

    @NotNull(message = "Service is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @NotNull(message = "Product ID is required")
    @Column(name = "service_product_id", nullable = false)
    private Long serviceProductId;
}
