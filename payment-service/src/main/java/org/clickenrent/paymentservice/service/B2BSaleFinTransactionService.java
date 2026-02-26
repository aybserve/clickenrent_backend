package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.dto.B2BSaleFinTransactionDTO;
import org.clickenrent.paymentservice.entity.B2BSaleFinTransaction;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.B2BSaleFinTransactionMapper;
import org.clickenrent.paymentservice.repository.B2BSaleFinTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for B2B Sale FinancialTransaction management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class B2BSaleFinTransactionService {

    private final B2BSaleFinTransactionRepository b2bSaleFinTransactionRepository;
    private final B2BSaleFinTransactionMapper b2bSaleFinTransactionMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<B2BSaleFinTransactionDTO> findAll() {
        if (!securityService.isAdmin() && !securityService.isB2B()) {
            throw new UnauthorizedException("You don't have permission to view B2B sale transactions");
        }
        return b2bSaleFinTransactionMapper.toDTOList(b2bSaleFinTransactionRepository.findAll());
    }

    @Transactional(readOnly = true)
    public B2BSaleFinTransactionDTO findById(Long id) {
        if (!securityService.isAdmin() && !securityService.isB2B()) {
            throw new UnauthorizedException("You don't have permission to view B2B sale transactions");
        }
        
        B2BSaleFinTransaction transaction = b2bSaleFinTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleFinTransaction", "id", id));
        
        return b2bSaleFinTransactionMapper.toDTO(transaction);
    }

    @Transactional(readOnly = true)
    public B2BSaleFinTransactionDTO findByExternalId(String externalId) {
        if (!securityService.isAdmin() && !securityService.isB2B()) {
            throw new UnauthorizedException("You don't have permission to view B2B sale transactions");
        }
        
        B2BSaleFinTransaction transaction = b2bSaleFinTransactionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleFinTransaction", "externalId", externalId));
        
        return b2bSaleFinTransactionMapper.toDTO(transaction);
    }

    @Transactional
    public B2BSaleFinTransactionDTO create(B2BSaleFinTransactionDTO dto) {
        // External ID is provided directly in the DTO
        log.debug("Creating B2B sale transaction with b2bSaleExternalId: {}", dto.getB2bSaleExternalId());
        
        B2BSaleFinTransaction transaction = b2bSaleFinTransactionMapper.toEntity(dto);
        transaction.sanitizeForCreate();
        B2BSaleFinTransaction savedTransaction = b2bSaleFinTransactionRepository.save(transaction);
        return b2bSaleFinTransactionMapper.toDTO(savedTransaction);
    }

    @Transactional
    public B2BSaleFinTransactionDTO update(Long id, B2BSaleFinTransactionDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can update B2B sale transactions");
        }
        
        B2BSaleFinTransaction existingTransaction = b2bSaleFinTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleFinTransaction", "id", id));
        
        existingTransaction.setB2bSaleExternalId(dto.getB2bSaleExternalId());
        
        B2BSaleFinTransaction updatedTransaction = b2bSaleFinTransactionRepository.save(existingTransaction);
        return b2bSaleFinTransactionMapper.toDTO(updatedTransaction);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete B2B sale transactions");
        }
        
        if (!b2bSaleFinTransactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("B2BSaleFinTransaction", "id", id);
        }
        b2bSaleFinTransactionRepository.deleteById(id);
    }
}




