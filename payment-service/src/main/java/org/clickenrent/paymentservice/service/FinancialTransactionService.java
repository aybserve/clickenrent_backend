package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.dto.FinancialTransactionDTO;
import org.clickenrent.paymentservice.entity.FinancialTransaction;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.FinancialTransactionMapper;
import org.clickenrent.paymentservice.repository.FinancialTransactionRepository;
import org.clickenrent.paymentservice.repository.PaymentStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for FinancialTransaction management with Stripe integration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialTransactionService {

    private final FinancialTransactionRepository financialTransactionRepository;
    private final FinancialTransactionMapper financialTransactionMapper;
    private final PaymentStatusRepository paymentStatusRepository;
    private final SecurityService securityService;
    private final StripeService stripeService;

    @Transactional(readOnly = true)
    public List<FinancialTransactionDTO> findAll() {
        List<FinancialTransaction> transactions = financialTransactionRepository.findAll();
        
        // Filter by permissions
        if (!securityService.isAdmin()) {
            Long currentUserId = securityService.getCurrentUserId();
            transactions = transactions.stream()
                    .filter(t -> t.getPayerId().equals(currentUserId) || t.getRecipientId().equals(currentUserId))
                    .collect(Collectors.toList());
        }
        
        return financialTransactionMapper.toDTOList(transactions);
    }

    @Transactional(readOnly = true)
    public FinancialTransactionDTO findById(Long id) {
        FinancialTransaction transaction = financialTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialTransaction", "id", id));
        
        // Check permission
        checkTransactionAccess(transaction);
        
        return financialTransactionMapper.toDTO(transaction);
    }

    @Transactional(readOnly = true)
    public FinancialTransactionDTO findByExternalId(UUID externalId) {
        FinancialTransaction transaction = financialTransactionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialTransaction", "externalId", externalId));
        
        checkTransactionAccess(transaction);
        
        return financialTransactionMapper.toDTO(transaction);
    }

    @Transactional(readOnly = true)
    public List<FinancialTransactionDTO> findByPayerId(Long payerId) {
        // Check permission
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(payerId)) {
            throw new UnauthorizedException("You don't have permission to access these transactions");
        }
        
        List<FinancialTransaction> transactions = financialTransactionRepository.findByPayerId(payerId);
        return financialTransactionMapper.toDTOList(transactions);
    }

    @Transactional
    public FinancialTransactionDTO create(FinancialTransactionDTO dto) {
        FinancialTransaction transaction = financialTransactionMapper.toEntity(dto);
        FinancialTransaction savedTransaction = financialTransactionRepository.save(transaction);
        return financialTransactionMapper.toDTO(savedTransaction);
    }

    @Transactional
    public FinancialTransactionDTO processPayment(FinancialTransactionDTO dto) {
        log.info("Processing payment for amount: {} {}", dto.getAmount(), dto.getCurrency().getCode());
        
        try {
            // Create payment intent in Stripe
            String paymentIntentId = stripeService.createPaymentIntent(
                    dto.getAmount(),
                    dto.getCurrency().getCode(),
                    null // customerId can be added if available
            );
            
            dto.setStripePaymentIntentId(paymentIntentId);
            
            // Confirm payment intent
            String chargeId = stripeService.confirmPaymentIntent(paymentIntentId);
            dto.setStripeChargeId(chargeId);
            
            // Update status to SUCCEEDED
            var succeededStatus = paymentStatusRepository.findByCode("SUCCEEDED")
                    .orElseThrow(() -> new ResourceNotFoundException("PaymentStatus", "code", "SUCCEEDED"));
            dto.getPaymentStatus().setId(succeededStatus.getId());
            
            // Save transaction
            FinancialTransaction transaction = financialTransactionMapper.toEntity(dto);
            FinancialTransaction savedTransaction = financialTransactionRepository.save(transaction);
            
            log.info("Payment processed successfully. Transaction ID: {}", savedTransaction.getId());
            return financialTransactionMapper.toDTO(savedTransaction);
            
        } catch (Exception e) {
            log.error("Payment processing failed", e);
            
            // Update status to FAILED
            var failedStatus = paymentStatusRepository.findByCode("FAILED")
                    .orElseThrow(() -> new ResourceNotFoundException("PaymentStatus", "code", "FAILED"));
            dto.getPaymentStatus().setId(failedStatus.getId());
            
            FinancialTransaction transaction = financialTransactionMapper.toEntity(dto);
            financialTransactionRepository.save(transaction);
            
            throw new RuntimeException("Payment processing failed: " + e.getMessage(), e);
        }
    }

    @Transactional
    public FinancialTransactionDTO refundTransaction(Long transactionId, BigDecimal amount) {
        FinancialTransaction originalTransaction = financialTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialTransaction", "id", transactionId));
        
        // Check permission
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can process refunds");
        }
        
        log.info("Processing refund for transaction: {}, amount: {}", transactionId, amount);
        
        try {
            // Create refund in Stripe
            String refundId = stripeService.createRefund(
                    originalTransaction.getStripeChargeId(),
                    amount
            );
            
            // Determine refund status
            boolean isPartialRefund = amount != null && amount.compareTo(originalTransaction.getAmount()) < 0;
            String statusCode = isPartialRefund ? "PARTIALLY_REFUNDED" : "REFUNDED";
            
            var refundStatus = paymentStatusRepository.findByCode(statusCode)
                    .orElseThrow(() -> new ResourceNotFoundException("PaymentStatus", "code", statusCode));
            
            // Create refund transaction
            FinancialTransaction refundTransaction = FinancialTransaction.builder()
                    .payerId(originalTransaction.getRecipientId()) // Reversed
                    .recipientId(originalTransaction.getPayerId()) // Reversed
                    .amount(amount != null ? amount : originalTransaction.getAmount())
                    .currency(originalTransaction.getCurrency())
                    .paymentMethod(originalTransaction.getPaymentMethod())
                    .paymentStatus(refundStatus)
                    .serviceProvider(originalTransaction.getServiceProvider())
                    .stripeRefundId(refundId)
                    .originalTransactionId(originalTransaction.getId())
                    .build();
            
            FinancialTransaction savedRefund = financialTransactionRepository.save(refundTransaction);
            
            // Update original transaction status
            originalTransaction.setPaymentStatus(refundStatus);
            financialTransactionRepository.save(originalTransaction);
            
            log.info("Refund processed successfully. Refund transaction ID: {}", savedRefund.getId());
            return financialTransactionMapper.toDTO(savedRefund);
            
        } catch (Exception e) {
            log.error("Refund processing failed", e);
            throw new RuntimeException("Refund processing failed: " + e.getMessage(), e);
        }
    }

    @Transactional
    public FinancialTransactionDTO update(Long id, FinancialTransactionDTO dto) {
        FinancialTransaction existingTransaction = financialTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialTransaction", "id", id));
        
        // Check permission
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can update transactions");
        }
        
        // Update fields (typically only status would be updated)
        existingTransaction.setPaymentStatus(financialTransactionMapper.toEntity(dto).getPaymentStatus());
        
        FinancialTransaction updatedTransaction = financialTransactionRepository.save(existingTransaction);
        return financialTransactionMapper.toDTO(updatedTransaction);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete transactions");
        }
        
        if (!financialTransactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("FinancialTransaction", "id", id);
        }
        financialTransactionRepository.deleteById(id);
    }

    private void checkTransactionAccess(FinancialTransaction transaction) {
        if (!securityService.isAdmin() && 
            !securityService.hasAccessToUser(transaction.getPayerId()) &&
            !securityService.hasAccessToUser(transaction.getRecipientId())) {
            throw new UnauthorizedException("You don't have permission to access this transaction");
        }
    }
}

