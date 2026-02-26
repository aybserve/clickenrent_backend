package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.dto.CreateRefundRequestDTO;
import org.clickenrent.paymentservice.dto.RefundDTO;
import org.clickenrent.paymentservice.entity.*;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.RefundMapper;
import org.clickenrent.paymentservice.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Refund management with payment provider integration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefundService {

    private final RefundRepository refundRepository;
    private final RefundMapper refundMapper;
    private final RefundStatusRepository refundStatusRepository;
    private final RefundReasonRepository refundReasonRepository;
    private final FinancialTransactionRepository financialTransactionRepository;
    private final CurrencyRepository currencyRepository;
    private final SecurityService securityService;
    private final PaymentProviderService paymentProviderService;

    @Transactional(readOnly = true)
    public List<RefundDTO> findAll() {
        // Only admins can view all refunds
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can view all refunds");
        }
        
        List<Refund> refunds = refundRepository.findAll();
        return refundMapper.toDTOList(refunds);
    }

    @Transactional(readOnly = true)
    public RefundDTO findById(Long id) {
        Refund refund = refundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund", "id", id));
        
        // Check permission
        checkRefundAccess(refund);
        
        return refundMapper.toDTO(refund);
    }

    @Transactional(readOnly = true)
    public RefundDTO findByExternalId(String externalId) {
        Refund refund = refundRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund", "externalId", externalId));
        
        checkRefundAccess(refund);
        
        return refundMapper.toDTO(refund);
    }

    @Transactional(readOnly = true)
    public List<RefundDTO> findByFinancialTransactionId(Long transactionId) {
        // Check permission - only admins for now
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to access these refunds");
        }
        
        List<Refund> refunds = refundRepository.findByFinancialTransactionId(transactionId);
        return refundMapper.toDTOList(refunds);
    }

    @Transactional
    public RefundDTO createRefund(CreateRefundRequestDTO request) {
        log.info("Creating refund for transaction: {}, amount: {} {}", 
                request.getFinancialTransactionId(), request.getAmount(), request.getCurrencyCode());
        
        // Check permission
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can process refunds");
        }
        
        // Fetch original transaction
        FinancialTransaction originalTransaction = financialTransactionRepository
                .findById(request.getFinancialTransactionId())
                .orElseThrow(() -> new ResourceNotFoundException("FinancialTransaction", "id", 
                        request.getFinancialTransactionId()));
        
        // Fetch currency
        Currency currency = currencyRepository.findByCode(request.getCurrencyCode())
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "code", request.getCurrencyCode()));
        
        // Validate refund amount
        if (request.getAmount().compareTo(originalTransaction.getAmount()) > 0) {
            throw new IllegalArgumentException("Refund amount cannot exceed original transaction amount");
        }
        
        // Get refund reason if provided
        RefundReason refundReason = null;
        if (request.getRefundReasonCode() != null) {
            refundReason = refundReasonRepository.findByCode(request.getRefundReasonCode())
                    .orElseThrow(() -> new ResourceNotFoundException("RefundReason", "code", 
                            request.getRefundReasonCode()));
        }
        
        // Set initial status to PROCESSING
        RefundStatus processingStatus = refundStatusRepository.findByCode("PROCESSING")
                .orElseThrow(() -> new ResourceNotFoundException("RefundStatus", "code", "PROCESSING"));
        
        // Create refund entity
        Long currentUserId = securityService.getCurrentUserId();
        Refund refund = Refund.builder()
                .financialTransaction(originalTransaction)
                .amount(request.getAmount())
                .currency(currency)
                .refundStatus(processingStatus)
                .refundReason(refundReason)
                .description(request.getDescription())
                .initiatedByExternalId(currentUserId != null ? currentUserId.toString() : null)
                .companyExternalId(originalTransaction.getCompanyExternalId())
                .build();
        
        // Save refund to get ID
        refund = refundRepository.save(refund);
        
        try {
            // Determine charge/order ID based on provider
            String chargeId;
            if (originalTransaction.getStripeChargeId() != null) {
                chargeId = originalTransaction.getStripeChargeId();
            } else if (originalTransaction.getMultiSafepayOrderId() != null) {
                chargeId = originalTransaction.getMultiSafepayOrderId();
            } else {
                throw new IllegalStateException("No payment provider charge/order ID found");
            }
            
            // Process refund with payment provider
            String providerRefundId = paymentProviderService.createRefund(
                    chargeId,
                    request.getAmount(),
                    request.getCurrencyCode(),
                    request.getDescription() != null ? request.getDescription() : 
                            "Refund for transaction " + request.getFinancialTransactionId()
            );
            
            // Update refund with provider ID and success status
            if (paymentProviderService.isStripeActive()) {
                refund.setStripeRefundId(providerRefundId);
            } else if (paymentProviderService.isMultiSafepayActive()) {
                refund.setMultisafepayRefundId(providerRefundId);
            }
            
            RefundStatus succeededStatus = refundStatusRepository.findByCode("SUCCEEDED")
                    .orElseThrow(() -> new ResourceNotFoundException("RefundStatus", "code", "SUCCEEDED"));
            refund.setRefundStatus(succeededStatus);
            refund.setProcessedAt(LocalDateTime.now());
            
            refund = refundRepository.save(refund);
            
            log.info("Refund created successfully with {}. Refund ID: {}", 
                    paymentProviderService.getActiveProvider(), refund.getId());
            return refundMapper.toDTO(refund);
            
        } catch (Exception e) {
            log.error("Refund processing failed", e);
            
            // Update status to FAILED
            RefundStatus failedStatus = refundStatusRepository.findByCode("FAILED")
                    .orElseThrow(() -> new ResourceNotFoundException("RefundStatus", "code", "FAILED"));
            refund.setRefundStatus(failedStatus);
            refundRepository.save(refund);
            
            throw new RuntimeException("Refund processing failed: " + e.getMessage(), e);
        }
    }

    @Transactional
    public RefundDTO updateRefundStatus(Long id, String statusCode) {
        // Only admins can update refund status
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can update refund status");
        }
        
        Refund refund = refundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund", "id", id));
        
        RefundStatus newStatus = refundStatusRepository.findByCode(statusCode)
                .orElseThrow(() -> new ResourceNotFoundException("RefundStatus", "code", statusCode));
        
        refund.setRefundStatus(newStatus);
        
        if ("SUCCEEDED".equals(statusCode) && refund.getProcessedAt() == null) {
            refund.setProcessedAt(LocalDateTime.now());
        }
        
        refund = refundRepository.save(refund);
        
        log.info("Refund status updated to {} for refund ID: {}", statusCode, id);
        return refundMapper.toDTO(refund);
    }

    @Transactional
    public void deleteByExternalId(String externalId) {
        // Only SUPERADMIN can delete refunds (soft delete)
        List<String> roles = securityService.getCurrentUserRoles();
        if (!roles.contains("SUPERADMIN")) {
            throw new UnauthorizedException("Only superadmins can delete refunds");
        }
        
        Refund refund = refundRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund", "externalId", externalId));
        
        refundRepository.delete(refund);
        log.warn("AUDIT: Refund soft deleted: {} by user: {}", externalId, securityService.getCurrentUserId());
    }

    private void checkRefundAccess(Refund refund) {
        // Admins can access all refunds
        if (securityService.isAdmin()) {
            return;
        }
        
        // For non-admins, check if they have access to this refund
        // This would typically check if the user is part of the company
        List<String> currentUserCompanies = securityService.getCurrentUserCompanyExternalIds();
        if (refund.getCompanyExternalId() == null || 
            !currentUserCompanies.contains(refund.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to access this refund");
        }
    }
}
