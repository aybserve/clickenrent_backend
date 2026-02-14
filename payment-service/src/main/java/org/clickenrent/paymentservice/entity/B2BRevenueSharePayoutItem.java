package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * B2B revenue share payout item entity (individual bike rental in payout)
 */
@Entity
@Table(name = "b2b_revenue_share_payout_items")
@SQLDelete(sql = "UPDATE b2b_revenue_share_payout_items SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class B2BRevenueSharePayoutItem extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_revenue_share_payout_id", nullable = false)
    private B2BRevenueSharePayout b2bRevenueSharePayout;

    @Column(name = "bike_rental_external_id", length = 100)
    private String bikeRentalExternalId;

    @Column(name = "bike_rental_total_price", precision = 19, scale = 2)
    private BigDecimal bikeRentalTotalPrice;

    @Column(name = "revenue_share_percent", precision = 5, scale = 2)
    private BigDecimal revenueSharePercent;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

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
        if (!(o instanceof B2BRevenueSharePayoutItem)) return false;
        B2BRevenueSharePayoutItem that = (B2BRevenueSharePayoutItem) o;
        return externalId != null && externalId.equals(that.externalId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}




