package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.UserPaymentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for UserPaymentProfile entity
 */
@Repository
public interface UserPaymentProfileRepository extends JpaRepository<UserPaymentProfile, Long> {
    
    Optional<UserPaymentProfile> findByUserId(Long userId);
    
    Optional<UserPaymentProfile> findByStripeCustomerId(String stripeCustomerId);
    
    Optional<UserPaymentProfile> findByExternalId(UUID externalId);
}

