package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.UserPaymentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UserPaymentProfile entity
 */
@Repository
public interface UserPaymentProfileRepository extends JpaRepository<UserPaymentProfile, Long> {
    
    Optional<UserPaymentProfile> findByUserExternalId(String userExternalId);
    
    Optional<UserPaymentProfile> findByStripeCustomerId(String stripeCustomerId);
    
    Optional<UserPaymentProfile> findByExternalId(String externalId);
}




