package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

/**
 * Junction entity linking rentals to financial transactions
 */
@Entity
@Table(name = "rental_fin_transactions")
@SQLDelete(sql = "UPDATE rental_fin_transactions SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RentalFinTransaction extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Column(name = "rental_external_id", length = 100)
    private String rentalExternalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_transaction_id", nullable = false)
    private FinancialTransaction financialTransaction;

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
        if (!(o instanceof RentalFinTransaction)) return false;
        RentalFinTransaction that = (RentalFinTransaction) o;
        return externalId != null && externalId.equals(that.externalId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}




