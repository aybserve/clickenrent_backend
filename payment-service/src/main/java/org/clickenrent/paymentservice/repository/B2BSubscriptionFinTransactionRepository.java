package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.B2BSubscriptionFinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for B2BSubscriptionFinTransaction entity
 */
@Repository
public interface B2BSubscriptionFinTransactionRepository extends JpaRepository<B2BSubscriptionFinTransaction, Long> {
    
    Optional<B2BSubscriptionFinTransaction> findByB2bSubscriptionId(Long b2bSubscriptionId);
    
    Optional<B2BSubscriptionFinTransaction> findByExternalId(String externalId);
}


