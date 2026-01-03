package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Junction entity linking B2B revenue share payouts to financial transactions
 */
@Entity
@Table(name = "payout_fin_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PayoutFinTransaction extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_revenue_share_payout_id", nullable = false)
    private B2BRevenueSharePayout b2bRevenueSharePayout;

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
        if (!(o instanceof PayoutFinTransaction)) return false;
        PayoutFinTransaction that = (PayoutFinTransaction) o;
        return externalId != null && externalId.equals(that.externalId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}








