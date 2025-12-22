package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a bike rental
 */
@Entity
@Table(
        name = "bike_rental",
        indexes = {
                @Index(name = "idx_bike_rental_external_id", columnList = "external_id"),
                @Index(name = "idx_bike_rental_bike_external_id", columnList = "bike_external_id"),
                @Index(name = "idx_bike_rental_location_external_id", columnList = "location_external_id"),
                @Index(name = "idx_bike_rental_rental_external_id", columnList = "rental_external_id")
        }
)
@SQLDelete(sql = "UPDATE bike_rental SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class BikeRental extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "Bike is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    // Cross-service reference field using externalId
    @Column(name = "bike_external_id", length = 100)
    private String bikeExternalId;

    @NotNull(message = "Location is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    // Cross-service reference field using externalId
    @Column(name = "location_external_id", length = 100)
    private String locationExternalId;

    @NotNull(message = "Rental is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    // Cross-service reference field using externalId
    @Column(name = "rental_external_id", length = 100)
    private String rentalExternalId;

    @NotNull(message = "Start date time is required")
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_unit_id")
    private RentalUnit rentalUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_rental_status_id")
    private BikeRentalStatus bikeRentalStatus;

    @Builder.Default
    @Column(name = "is_revenue_share_paid", nullable = false)
    private Boolean isRevenueSharePaid = false;

    @Builder.Default
    @Column(name = "is_b2b_rentable", nullable = false)
    private Boolean isB2BRentable = false;

    @Size(max = 500, message = "Photo URL must not exceed 500 characters")
    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Total price is required")
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }
}

