package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

/**
 * Abstract base class for all product types.
 * Uses JOINED inheritance strategy for clean separation of concerns.
 * Subclasses: BikeModel, ChargingStationModel, Part, ServiceProduct, Bike, ChargingStation
 */
@Entity
@Table(
    name = "product",
    indexes = {
        @Index(name = "idx_product_external_id", columnList = "external_id"),
        @Index(name = "idx_product_type", columnList = "product_type")
    }
)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "product_type", discriminatorType = DiscriminatorType.STRING)
@SQLDelete(sql = "UPDATE product SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
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

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
        if (getIsDeleted() == null) {
            setIsDeleted(false);
        }
    }
}

