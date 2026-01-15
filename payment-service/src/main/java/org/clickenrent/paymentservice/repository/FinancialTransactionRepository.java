package org.clickenrent.paymentservice.repository;

import org.clickenrent.paymentservice.entity.FinancialTransaction;
import org.clickenrent.paymentservice.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * Repository for FinancialTransaction entity
 */
@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
    
    List<FinancialTransaction> findByPayerExternalId(String payerExternalId);
    
    List<FinancialTransaction> findByRecipientExternalId(String recipientExternalId);
    
    Optional<FinancialTransaction> findByStripePaymentIntentId(String stripePaymentIntentId);
    
    Optional<FinancialTransaction> findByMultiSafepayOrderId(String multiSafepayOrderId);
    
    Optional<FinancialTransaction> findByExternalId(String externalId);
    
    List<FinancialTransaction> findByPaymentStatus(PaymentStatus status);
}




