package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing bike reservations.
 */
@Entity
@Table(
    name = "bike_reservation",
    indexes = {
        @Index(name = "idx_bike_reservation_external_id", columnList = "external_id"),
        @Index(name = "idx_bike_reservation_user_external_id", columnList = "user_external_id")
    }
)
@SQLDelete(sql = "UPDATE bike_reservation SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class BikeReservation extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "Start date time is required")
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @NotNull(message = "End date time is required")
    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @NotNull(message = "User external ID is required")
    @Column(name = "user_external_id", nullable = false, length = 100)
    private String userExternalId;

    @NotNull(message = "Bike is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }
}
