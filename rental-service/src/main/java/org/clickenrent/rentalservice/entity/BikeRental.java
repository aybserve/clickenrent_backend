package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Entity representing a bike rental (extends Product).
 * Links a bike to a rental with dates.
 * Uses SINGLE_TABLE inheritance.
 */
@Entity
@DiscriminatorValue("BIKE_RENTAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BikeRental extends Product {

    @NotNull(message = "Bike is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    @NotNull(message = "Location is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @NotNull(message = "Rental is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    @NotNull(message = "Start date time is required")
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_unit_id")
    private RentalUnit rentalUnit;

    @Builder.Default
    @Column(name = "is_revenue_share_paid", nullable = false)
    private Boolean isRevenueSharePaid = false;
}
