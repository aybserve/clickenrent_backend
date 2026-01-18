package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.clickenrent.contracts.security.TenantScoped;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

/**
 * Entity representing bank account information for a location
 * Used for MultiSafepay payout processing
 */
@Entity
@Table(
    name = "location_bank_accounts",
    indexes = {
        @Index(name = "idx_location_bank_account_external_id", columnList = "external_id"),
        @Index(name = "idx_location_bank_account_location_external_id", columnList = "location_external_id"),
        @Index(name = "idx_location_bank_account_company_external_id", columnList = "company_external_id")
    }
)
@Filter(name = "companyFilter", condition = "company_external_id IN (:companyExternalIds)")
@SQLDelete(sql = "UPDATE location_bank_accounts SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LocationBankAccount extends BaseAuditEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Column(name = "company_external_id", length = 100)
    private String companyExternalId;

    @NotBlank(message = "Location external ID is required")
    @Column(name = "location_external_id", nullable = false, length = 100)
    private String locationExternalId;

    @NotBlank(message = "Account holder name is required")
    @Size(max = 255, message = "Account holder name must not exceed 255 characters")
    @Column(name = "account_holder_name", nullable = false, length = 255)
    private String accountHolderName;

    @NotBlank(message = "IBAN is required")
    @Size(max = 34, message = "IBAN must not exceed 34 characters")
    @Column(name = "iban", nullable = false, length = 34)
    private String iban;

    @Size(max = 11, message = "BIC must not exceed 11 characters")
    @Column(name = "bic", length = 11)
    private String bic;

    @NotBlank(message = "Currency is required")
    @Size(max = 3, message = "Currency must be 3 characters")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Size(max = 1000, message = "Verification notes must not exceed 1000 characters")
    @Column(name = "verification_notes", length = 1000)
    private String verificationNotes;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationBankAccount)) return false;
        LocationBankAccount that = (LocationBankAccount) o;
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
