package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.PayoutFinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for PayoutFinTransaction entity
 */
@Repository
public interface PayoutFinTransactionRepository extends JpaRepository<PayoutFinTransaction, Long> {
    
    Optional<PayoutFinTransaction> findByB2bRevenueSharePayoutId(Long payoutId);
    
    Optional<PayoutFinTransaction> findByExternalId(UUID externalId);
}
