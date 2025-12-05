package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity representing the relationship between bike models and rental plans with pricing.
 */
@Entity
@Table(name = "bike_model_rental_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class BikeModelRentalPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Bike model is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_model_id", nullable = false)
    private BikeModel bikeModel;

    @NotNull(message = "Rental plan is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_plan_id", nullable = false)
    private RentalPlan rentalPlan;

    @NotNull(message = "Price is required")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
