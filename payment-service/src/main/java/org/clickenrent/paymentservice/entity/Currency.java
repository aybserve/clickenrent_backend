package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

/**
 * Currency entity representing different currencies (USD, EUR, etc.)
 */
@Entity
@Table(name = "currencies")
@SQLDelete(sql = "UPDATE currencies SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Currency extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Column(unique = true, nullable = false, length = 3)
    private String code; // USD, EUR, GBP, etc.

    @Column(nullable = false)
    private String name; // US Dollar, Euro, British Pound, etc.
    
    @Column(length = 10)
    private String symbol; // $, €, £, etc.

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;
        Currency currency = (Currency) o;
        return code != null && code.equals(currency.code);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}




