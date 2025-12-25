package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity representing bike engine types.
 */
@Entity
@Table(
    name = "bike_engine",
    indexes = {
        @Index(name = "idx_bike_engine_external_id", columnList = "external_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class BikeEngine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotBlank(message = "Bike engine name is required")
    @Size(max = 100, message = "Bike engine name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;
}






