package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity representing bike types.
 * Examples: Electric bike, Non-electric bike
 */
@Entity
@Table(name = "bike_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class BikeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Bike type name is required")
    @Size(max = 100, message = "Bike type name must not exceed 100 characters")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;
}
