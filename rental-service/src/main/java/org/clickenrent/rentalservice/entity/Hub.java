package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity representing a hub within a location.
 * Auto-creates "Main" hub when a location is created.
 */
@Entity
@Table(
    name = "hub",
    indexes = {
        @Index(name = "idx_hub_external_id", columnList = "external_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Hub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotBlank(message = "Hub name is required")
    @Size(max = 255, message = "Hub name must not exceed 255 characters")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @NotNull(message = "Location is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Size(max = 1000, message = "Directions must not exceed 1000 characters")
    @Column(name = "directions", length = 1000)
    private String directions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinates_id")
    private Coordinates coordinates;
}
