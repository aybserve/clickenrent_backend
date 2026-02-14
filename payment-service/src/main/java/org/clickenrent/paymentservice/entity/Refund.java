package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.clickenrent.contracts.security.TenantScoped;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Refund entity for tracking refund operations.
 * Implements TenantScoped for multi-tenant isolation.
 */
@Entity
@Table(
    name = "refunds",
    indexes = {
        @Index(name = "idx_refund_company", columnList = "company_external_id"),
        @Index(name = "idx_refund_financial_transaction", columnList = "financial_transaction_id"),
        @Index(name = "idx_refund_status", columnList = "refund_status_id")
    }
)
@Filter(name = "companyFilter", condition = "company_external_id IN (:companyExternalIds)")
@SQLDelete(sql = "UPDATE refunds SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Refund extends BaseAuditEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "financial_transaction_id", nullable = false)
    private FinancialTransaction financialTransaction;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "refund_status_id", nullable = false)
    private RefundStatus refundStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "refund_reason_id")
    private RefundReason refundReason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "initiated_by_external_id", length = 100)
    private String initiatedByExternalId;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "stripe_refund_id", length = 255)
    private String stripeRefundId;

    @Column(name = "multisafepay_refund_id", length = 255)
    private String multisafepayRefundId;

    @Column(name = "company_external_id", length = 100)
    private String companyExternalId;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Refund)) return false;
        Refund that = (Refund) o;
        return externalId != null && externalId.equals(that.externalId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String getCompanyExternalId() {
        return this.companyExternalId;
    }
}
