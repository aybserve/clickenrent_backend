package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity representing charging station models.
 */
@Entity
@Table(
    name = "charging_station_model",
    indexes = {
        @Index(name = "idx_charging_station_model_external_id", columnList = "external_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class ChargingStationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

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
}
