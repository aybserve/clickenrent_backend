package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/**
 * Entity representing user-location associations with roles.
 */
@Entity
@Table(
    name = "user_location",
    indexes = {
        @Index(name = "idx_user_location_external_id", columnList = "external_id"),
        @Index(name = "idx_user_location_user_id", columnList = "user_id"),
        @Index(name = "idx_user_location_user_external_id", columnList = "user_external_id")
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

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Cross-service reference field using externalId
    @Column(name = "user_external_id", length = 100)
    private String userExternalId;

    @NotNull(message = "Location is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @NotNull(message = "Location role is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_role_id", nullable = false)
    private LocationRole locationRole;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }
}


