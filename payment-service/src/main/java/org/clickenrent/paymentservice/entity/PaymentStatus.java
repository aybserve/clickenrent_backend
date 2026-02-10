package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

/**
 * Payment status entity (PENDING, SUCCEEDED, FAILED, etc.)
 */
@Entity
@Table(name = "payment_statuses")
@SQLDelete(sql = "UPDATE payment_statuses SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentStatus extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Column(unique = true, nullable = false, length = 50)
    private String code; // PENDING, SUCCEEDED, FAILED, CANCELED, REFUNDED, PARTIALLY_REFUNDED

    @Column(nullable = false)
    private String name;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getExternalId() {
        return externalId;
    }

    @Override
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentStatus)) return false;
        PaymentStatus that = (PaymentStatus) o;
        return code != null && code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}




