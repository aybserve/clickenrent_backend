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
//@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ServiceProduct extends Product implements ProductModelType {

//    @NotNull(message = "Service is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = true)
    private Service service;

//    @NotNull(message = "Product ID is required")
    @Column(name = "product_id", nullable = true)
    private Long productId;

    @Override
    public String getProductModelTypeName() {
        return "SERVICE_PRODUCT";
    }
}
