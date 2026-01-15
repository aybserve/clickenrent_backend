package org.clickenrent.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * User payment method entity (stored payment methods for users)
 */
@Entity
@Table(name = "user_payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserPaymentMethod extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_payment_profile_id", nullable = false)
    private UserPaymentProfile userPaymentProfile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(length = 255)
    private String stripePaymentMethodId;

    @Column(length = 255)
    private String multiSafepayTokenId;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDefault = false;

    @Builder.Default
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
        if (!(o instanceof UserPaymentMethod)) return false;
        UserPaymentMethod that = (UserPaymentMethod) o;
        return externalId != null && externalId.equals(that.externalId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}








