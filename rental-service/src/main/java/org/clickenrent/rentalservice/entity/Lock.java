package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity representing bike locks.
 * One lock per bike, multiple keys per lock.
 */
@Entity
@Table(
    name = "lock_entity",
    indexes = {
        @Index(name = "idx_lock_external_id", columnList = "external_id"),
        @Index(name = "idx_lock_mac_address", columnList = "mac_address")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Lock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotBlank(message = "MAC address is required")
    @Size(max = 17, message = "MAC address must not exceed 17 characters")
    @Column(name = "mac_address", nullable = false, unique = true, length = 17)
    private String macAddress;
}
