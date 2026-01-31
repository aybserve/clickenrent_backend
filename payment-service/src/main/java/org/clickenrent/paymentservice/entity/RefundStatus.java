package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

/**
 * Refund status entity (PENDING, PROCESSING, SUCCEEDED, FAILED, CANCELED)
 */
@Entity
@Table(name = "refund_statuses")
@SQLDelete(sql = "UPDATE refund_statuses SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RefundStatus extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Column(unique = true, nullable = false, length = 50)
    private String code; // PENDING, PROCESSING, SUCCEEDED, FAILED, CANCELED

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
        if (!(o instanceof RefundStatus)) return false;
        RefundStatus that = (RefundStatus) o;
        return code != null && code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
