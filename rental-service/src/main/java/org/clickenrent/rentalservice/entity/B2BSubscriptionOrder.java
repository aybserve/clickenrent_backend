package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Entity representing B2B subscription orders.
 */
@Entity
@Table(
    name = "b2b_subscription_order",
    indexes = {
        @Index(name = "idx_b2b_subscription_order_external_id", columnList = "external_id")
    }
)
@SQLDelete(sql = "UPDATE b2b_subscription_order SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class B2BSubscriptionOrder extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "Location is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @NotNull(message = "Date time is required")
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @NotNull(message = "B2B subscription order status is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_subscription_order_status_id", nullable = false)
    private B2BSubscriptionOrderStatus b2bSubscriptionOrderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_subscription_id")
    private B2BSubscription b2bSubscription;
}




