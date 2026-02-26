package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Entity representing charging station models.
 * Extends Product using JOINED inheritance strategy.
 */
@Entity
@Table(name = "charging_station_model")
@DiscriminatorValue("CHARGING_STATION_MODEL")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ChargingStationModel extends Product {

    @NotBlank(message = "Charging station model name is required")
    @Size(max = 100, message = "Charging station model name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "Charging station brand is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_station_brand_id", nullable = false)
    private ChargingStationBrand chargingStationBrand;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @NotNull(message = "B2B sale price is required")
    @Column(name = "b2b_sale_price", nullable = false, precision = 5, scale = 2)
    private BigDecimal b2bSalePrice;

    @NotNull(message = "B2B subscription price is required")
    @Column(name = "b2b_subscription_price", nullable = false, precision = 5, scale = 2)
    private BigDecimal b2bSubscriptionPrice;
}
