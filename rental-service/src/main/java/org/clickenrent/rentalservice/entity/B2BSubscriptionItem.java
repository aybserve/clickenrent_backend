package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing items in a B2B subscription.
 */
@Entity
@Table(
    name = "b2b_subscription_item",
    indexes = {
        @Index(name = "idx_b2b_subscription_item_external_id", columnList = "external_id"),
        @Index(name = "idx_b2b_subscription_item_product_id", columnList = "product_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class B2BSubscriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "B2B subscription is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_subscription_id", nullable = false)
    private B2BSubscription b2bSubscription;

    @NotNull(message = "Product ID is required")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull(message = "Start date time is required")
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @NotNull(message = "Agreed monthly fee is required")
    @Column(name = "agreed_monthly_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal agreedMonthlyFee;
}
