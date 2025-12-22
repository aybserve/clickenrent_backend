package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

/**
 * Payment method entity (CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, etc.)
 */
@Entity
@Table(name = "payment_methods")
@SQLDelete(sql = "UPDATE payment_methods SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentMethod extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Column(unique = true, nullable = false, length = 50)
    private String code; // CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, etc.

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isActive = true;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentMethod)) return false;
        PaymentMethod that = (PaymentMethod) o;
        return code != null && code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}




