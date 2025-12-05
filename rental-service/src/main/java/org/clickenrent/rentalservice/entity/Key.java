package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entity representing keys for locks.
 * Multiple keys can exist per lock.
 */
@Entity
@Table(
    name = "key_entity",
    indexes = {
        @Index(name = "idx_key_external_id", columnList = "external_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Key {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "Lock is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lock_id", nullable = false)
    private Lock lock;
}
