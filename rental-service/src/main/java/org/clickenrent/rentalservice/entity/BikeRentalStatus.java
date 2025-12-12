package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity representing bike rental status.
 * Examples: Pending, Active, Completed, Cancelled
 */
@Entity
@Table(name = "bike_rental_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class BikeRentalStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Bike rental status name is required")
    @Size(max = 50, message = "Bike rental status name must not exceed 50 characters")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
}
