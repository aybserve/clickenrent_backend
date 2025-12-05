package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing B2B subscriptions.
 */
@Entity
@Table(
    name = "b2b_subscription",
    indexes = {
        @Index(name = "idx_b2b_subscription_external_id", columnList = "external_id"),
        @Index(name = "idx_b2b_subscription_company_id", columnList = "company_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class B2BSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "Company ID is required")
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @NotNull(message = "B2B subscription status is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_subscription_status_id", nullable = false)
    private B2BSubscriptionStatus b2bSubscriptionStatus;
}
