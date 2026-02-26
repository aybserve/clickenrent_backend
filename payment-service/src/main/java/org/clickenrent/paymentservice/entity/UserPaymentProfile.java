package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

/**
 * User payment profile entity linking users to Stripe customers.
 * Note: This entity is user-scoped, not company-scoped.
 * Access control is based on user_external_id matching the authenticated user.
 */
@Entity
@Table(name = "user_payment_profiles")
@SQLDelete(sql = "UPDATE user_payment_profiles SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserPaymentProfile extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Column(name = "user_external_id", length = 100)
    private String userExternalId;

    @Column(unique = true, length = 255)
    private String stripeCustomerId;

    @Column(length = 255)
    private String multiSafepayCustomerId;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

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
        if (!(o instanceof UserPaymentProfile)) return false;
        UserPaymentProfile that = (UserPaymentProfile) o;
        return externalId != null && externalId.equals(that.externalId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}




