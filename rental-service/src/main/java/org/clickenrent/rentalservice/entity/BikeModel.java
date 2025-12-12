package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

/**
 * Entity representing bike models.
 */
@Entity
@Table(
    name = "bike_model",
    indexes = {
        @Index(name = "idx_bike_model_external_id", columnList = "external_id")
    }
)
@SQLDelete(sql = "UPDATE bike_model SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class BikeModel extends BaseAuditEntity implements ProductModelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotBlank(message = "Bike model name is required")
    @Size(max = 100, message = "Bike model name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "Bike brand is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_brand_id", nullable = false)
    private BikeBrand bikeBrand;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @NotNull(message = "Bike type is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_type_id", nullable = false)
    private BikeType bikeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_engine_id")
    private BikeEngine bikeEngine;

    @NotNull(message = "B2B sale price is required")
    @Column(name = "b2b_sale_price", nullable = false, precision = 5, scale = 2)
    private BigDecimal b2bSalePrice;

    @NotNull(message = "B2B subscription price is required")
    @Column(name = "b2b_subscription_price", nullable = false, precision = 5, scale = 2)
    private BigDecimal b2bSubscriptionPrice;

    @Override
    public String getProductModelTypeName() {
        return "BIKE_MODEL";
    }
}
