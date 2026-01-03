package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Core financial transaction entity
 */
@Entity
@Table(name = "financial_transactions")
@SQLDelete(sql = "UPDATE financial_transactions SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FinancialTransaction extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Column(name = "payer_external_id", length = 100)
    private String payerExternalId;

    @Column(name = "recipient_external_id", length = 100)
    private String recipientExternalId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_status_id", nullable = false)
    private PaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_provider_id")
    private ServiceProvider serviceProvider;

    @Column(length = 255)
    private String stripePaymentIntentId;

    @Column(length = 255)
    private String stripeChargeId;

    @Column(length = 255)
    private String stripeRefundId;

    @Column
    private Long originalTransactionId; // Self-reference for refunds

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
        if (dateTime == null) {
            dateTime = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FinancialTransaction)) return false;
        FinancialTransaction that = (FinancialTransaction) o;
        return externalId != null && externalId.equals(that.externalId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}




