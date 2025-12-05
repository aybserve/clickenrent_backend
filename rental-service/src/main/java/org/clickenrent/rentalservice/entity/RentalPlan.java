package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity representing rental plans.
 */
@Entity
@Table(name = "rental_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class RentalPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Rental plan name is required")
    @Size(max = 100, message = "Rental plan name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "Rental unit is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_unit_id", nullable = false)
    private RentalUnit rentalUnit;

    @Column(name = "min_unit")
    private Integer minUnit;

    @Column(name = "max_unit")
    private Integer maxUnit;

    @NotNull(message = "Location is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
}
