package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

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
@SQLDelete(sql = "UPDATE lock_entity SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Lock extends BaseAuditEntity {

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
