package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

/**
 * Refund reason entity (CUSTOMER_REQUEST, DUPLICATE_CHARGE, FRAUDULENT, etc.)
 */
@Entity
@Table(name = "refund_reasons")
@SQLDelete(sql = "UPDATE refund_reasons SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RefundReason extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Column(unique = true, nullable = false, length = 50)
    private String code; // CUSTOMER_REQUEST, DUPLICATE_CHARGE, FRAUDULENT, PRODUCT_NOT_AVAILABLE, SERVICE_UNSATISFACTORY, OTHER

    @Column(nullable = false)
    private String name;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefundReason)) return false;
        RefundReason that = (RefundReason) o;
        return code != null && code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
