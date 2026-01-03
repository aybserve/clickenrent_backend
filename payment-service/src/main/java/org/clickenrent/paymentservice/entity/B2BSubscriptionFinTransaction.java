package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

/**
 * Junction entity linking B2B subscriptions to financial transactions
 */
@Entity
@Table(name = "b2b_subscription_fin_transactions")
@SQLDelete(sql = "UPDATE b2b_subscription_fin_transactions SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class B2BSubscriptionFinTransaction extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Column(name = "b2b_subscription_external_id", length = 100)
    private String b2bSubscriptionExternalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_transaction_id", nullable = false)
    private FinancialTransaction financialTransaction;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof B2BSubscriptionFinTransaction)) return false;
        B2BSubscriptionFinTransaction that = (B2BSubscriptionFinTransaction) o;
        return externalId != null && externalId.equals(that.externalId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}




