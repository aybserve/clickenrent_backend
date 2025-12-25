package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.UserPaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserPaymentMethod entity
 */
@Repository
public interface UserPaymentMethodRepository extends JpaRepository<UserPaymentMethod, Long> {
    
    List<UserPaymentMethod> findByUserPaymentProfileId(Long profileId);
    
    Optional<UserPaymentMethod> findByStripePaymentMethodId(String stripePaymentMethodId);
    
    Optional<UserPaymentMethod> findByUserPaymentProfileIdAndIsDefaultTrue(Long profileId);
    
    Optional<UserPaymentMethod> findByExternalId(String externalId);
}






