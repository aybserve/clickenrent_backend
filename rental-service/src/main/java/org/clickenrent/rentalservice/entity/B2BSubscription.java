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
 * Entity representing B2B subscriptions.
 */
@Entity
@Table(
    name = "b2b_subscription",
    indexes = {
        @Index(name = "idx_b2b_subscription_external_id", columnList = "external_id"),
        @Index(name = "idx_b2b_subscription_location_id", columnList = "location_id")
    }
)
@SQLDelete(sql = "UPDATE b2b_subscription SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class B2BSubscription extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "Location is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @NotNull(message = "B2B subscription status is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_subscription_status_id", nullable = false)
    private B2BSubscriptionStatus b2bSubscriptionStatus;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }
}
