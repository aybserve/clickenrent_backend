package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing bike reservations.
 */
@Entity
@Table(
    name = "bike_reservation",
    indexes = {
        @Index(name = "idx_bike_reservation_external_id", columnList = "external_id"),
        @Index(name = "idx_bike_reservation_user_id", columnList = "user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class BikeReservation {

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

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Bike is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;
}
