package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.PayoutFinTransactionDTO;
import org.clickenrent.paymentservice.entity.PayoutFinTransaction;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.PayoutFinTransactionMapper;
import org.clickenrent.paymentservice.repository.PayoutFinTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for Payout FinancialTransaction management
 */
@Service
@RequiredArgsConstructor
public class PayoutFinTransactionService {

    private final PayoutFinTransactionRepository payoutFinTransactionRepository;
    private final PayoutFinTransactionMapper payoutFinTransactionMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<PayoutFinTransactionDTO> findAll() {
        if (!securityService.isAdmin() && !securityService.isB2B()) {
            throw new UnauthorizedException("You don't have permission to view payout transactions");
        }
        return payoutFinTransactionMapper.toDTOList(payoutFinTransactionRepository.findAll());
    }

    @Transactional(readOnly = true)
    public PayoutFinTransactionDTO findById(Long id) {
        if (!securityService.isAdmin() && !securityService.isB2B()) {
            throw new UnauthorizedException("You don't have permission to view payout transactions");
        }
        
        PayoutFinTransaction transaction = payoutFinTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PayoutFinTransaction", "id", id));
        
        return payoutFinTransactionMapper.toDTO(transaction);
    }

    @Transactional(readOnly = true)
    public PayoutFinTransactionDTO findByExternalId(String externalId) {
        if (!securityService.isAdmin() && !securityService.isB2B()) {
            throw new UnauthorizedException("You don't have permission to view payout transactions");
        }
        
        PayoutFinTransaction transaction = payoutFinTransactionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("PayoutFinTransaction", "externalId", externalId));
        
        return payoutFinTransactionMapper.toDTO(transaction);
    }

    @Transactional
    public PayoutFinTransactionDTO create(PayoutFinTransactionDTO dto) {
        PayoutFinTransaction transaction = payoutFinTransactionMapper.toEntity(dto);
        PayoutFinTransaction savedTransaction = payoutFinTransactionRepository.save(transaction);
        return payoutFinTransactionMapper.toDTO(savedTransaction);
    }

    @Transactional
    public PayoutFinTransactionDTO update(Long id, PayoutFinTransactionDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can update payout transactions");
        }
        
        PayoutFinTransaction existingTransaction = payoutFinTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PayoutFinTransaction", "id", id));
        
        // Update fields as needed
        
        PayoutFinTransaction updatedTransaction = payoutFinTransactionRepository.save(existingTransaction);
        return payoutFinTransactionMapper.toDTO(updatedTransaction);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete payout transactions");
        }
        
        if (!payoutFinTransactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("PayoutFinTransaction", "id", id);
        }
        payoutFinTransactionRepository.deleteById(id);
    }
}








