package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.clickenrent.contracts.security.TenantScoped;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * B2B revenue share payout entity
 * Implements TenantScoped for multi-tenant isolation.
 */
@Entity
@Table(name = "b2b_revenue_share_payouts")
@Filter(name = "companyFilter", condition = "company_external_id IN (:companyExternalIds)")
@SQLDelete(sql = "UPDATE b2b_revenue_share_payouts SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class B2BRevenueSharePayout extends BaseAuditEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Column(name = "company_external_id", length = 100)
    private String companyExternalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_bank_account_id")
    private LocationBankAccount locationBankAccount;

    @Column(name = "multisafepay_payout_id", length = 100)
    private String multiSafepayPayoutId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_status_id", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(name = "payout_date")
    private LocalDate payoutDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal paidAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal remainingAmount;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "failure_reason", length = 1000)
    private String failureReason;

    @OneToMany(mappedBy = "b2bRevenueSharePayout", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<B2BRevenueSharePayoutItem> payoutItems = new ArrayList<>();

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
        if (!(o instanceof B2BRevenueSharePayout)) return false;
        B2BRevenueSharePayout that = (B2BRevenueSharePayout) o;
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




