package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity representing user-location associations with roles.
 */
@Entity
@Table(
    name = "user_location",
    indexes = {
        @Index(name = "idx_user_location_user_id", columnList = "user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class UserLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Location is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @NotNull(message = "Location role is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_role_id", nullable = false)
    private LocationRole locationRole;
}
