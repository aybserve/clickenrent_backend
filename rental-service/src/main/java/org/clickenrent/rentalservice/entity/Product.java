package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base class for all product types.
 * Uses SINGLE_TABLE inheritance strategy for best performance.
 * Subclasses: Bike, ChargingStation, Part, ServiceProduct
 */
@Entity
@Table(
    name = "product",
    indexes = {
        @Index(name = "idx_product_external_id", columnList = "external_id"),
        @Index(name = "idx_product_type", columnList = "product_type")
    }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "product_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
public abstract class Product extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Builder.Default
    @Column(name = "is_b2b_rentable", nullable = false)
    private Boolean isB2BRentable = false;
}

