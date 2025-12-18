package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.FinancialTransaction;
import org.clickenrent.paymentservice.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for FinancialTransaction entity
 */
@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
    
    List<FinancialTransaction> findByPayerId(Long payerId);
    
    List<FinancialTransaction> findByRecipientId(Long recipientId);
    
    Optional<FinancialTransaction> findByStripePaymentIntentId(String stripePaymentIntentId);
    
    Optional<FinancialTransaction> findByExternalId(UUID externalId);
    
    List<FinancialTransaction> findByPaymentStatus(PaymentStatus status);
}

