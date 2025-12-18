package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.client.RentalServiceClient;
import org.clickenrent.paymentservice.dto.B2BSubscriptionFinTransactionDTO;
import org.clickenrent.paymentservice.entity.B2BSubscriptionFinTransaction;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.B2BSubscriptionFinTransactionMapper;
import org.clickenrent.paymentservice.repository.B2BSubscriptionFinTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for B2B Subscription FinancialTransaction management
 */
@Service
@RequiredArgsConstructor
public class B2BSubscriptionFinTransactionService {

    private final B2BSubscriptionFinTransactionRepository b2bSubscriptionFinTransactionRepository;
    private final B2BSubscriptionFinTransactionMapper b2bSubscriptionFinTransactionMapper;
    private final SecurityService securityService;
    private final RentalServiceClient rentalServiceClient;

    @Transactional(readOnly = true)
    public List<B2BSubscriptionFinTransactionDTO> findAll() {
        if (!securityService.isAdmin() && !securityService.isB2B()) {
            throw new UnauthorizedException("You don't have permission to view B2B subscription transactions");
        }
        return b2bSubscriptionFinTransactionMapper.toDTOList(b2bSubscriptionFinTransactionRepository.findAll());
    }

    @Transactional(readOnly = true)
    public B2BSubscriptionFinTransactionDTO findById(Long id) {
        if (!securityService.isAdmin() && !securityService.isB2B()) {
            throw new UnauthorizedException("You don't have permission to view B2B subscription transactions");
        }
        
        B2BSubscriptionFinTransaction transaction = b2bSubscriptionFinTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionFinTransaction", "id", id));
        
        return b2bSubscriptionFinTransactionMapper.toDTO(transaction);
    }

    @Transactional(readOnly = true)
    public B2BSubscriptionFinTransactionDTO findByExternalId(UUID externalId) {
        if (!securityService.isAdmin() && !securityService.isB2B()) {
            throw new UnauthorizedException("You don't have permission to view B2B subscription transactions");
        }
        
        B2BSubscriptionFinTransaction transaction = b2bSubscriptionFinTransactionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionFinTransaction", "externalId", externalId));
        
        return b2bSubscriptionFinTransactionMapper.toDTO(transaction);
    }

    @Transactional
    public B2BSubscriptionFinTransactionDTO create(B2BSubscriptionFinTransactionDTO dto) {
        // Validate B2B subscription exists
        rentalServiceClient.checkB2BSubscriptionExists(dto.getB2bSubscriptionId());
        
        B2BSubscriptionFinTransaction transaction = b2bSubscriptionFinTransactionMapper.toEntity(dto);
        B2BSubscriptionFinTransaction savedTransaction = b2bSubscriptionFinTransactionRepository.save(transaction);
        return b2bSubscriptionFinTransactionMapper.toDTO(savedTransaction);
    }

    @Transactional
    public B2BSubscriptionFinTransactionDTO update(Long id, B2BSubscriptionFinTransactionDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can update B2B subscription transactions");
        }
        
        B2BSubscriptionFinTransaction existingTransaction = b2bSubscriptionFinTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionFinTransaction", "id", id));
        
        existingTransaction.setB2bSubscriptionId(dto.getB2bSubscriptionId());
        
        B2BSubscriptionFinTransaction updatedTransaction = b2bSubscriptionFinTransactionRepository.save(existingTransaction);
        return b2bSubscriptionFinTransactionMapper.toDTO(updatedTransaction);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete B2B subscription transactions");
        }
        
        if (!b2bSubscriptionFinTransactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("B2BSubscriptionFinTransaction", "id", id);
        }
        b2bSubscriptionFinTransactionRepository.deleteById(id);
    }
}

