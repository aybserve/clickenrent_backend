package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * Entity representing a charging station (extends Product).
 * Uses SINGLE_TABLE inheritance.
 */
@Entity
@DiscriminatorValue("CHARGING_STATION")
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ChargingStation extends Product {

//    @NotNull(message = "Charging station status is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_station_status_id", nullable = true)
    private ChargingStationStatus chargingStationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinates_id")
    private Coordinates coordinates;

    @Builder.Default
    @Column(name = "is_active", nullable = true)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_id")
    private Hub hub;

    @Column(name = "in_service_date")
    private LocalDate inServiceDate;

//    @NotNull(message = "Charging station model is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_station_model_id", nullable = true)
    private ChargingStationModel chargingStationModel;
}
