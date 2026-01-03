package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.dto.RentalFinTransactionDTO;
import org.clickenrent.paymentservice.entity.RentalFinTransaction;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.RentalFinTransactionMapper;
import org.clickenrent.paymentservice.repository.RentalFinTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for Rental FinancialTransaction management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RentalFinTransactionService {

    private final RentalFinTransactionRepository rentalFinTransactionRepository;
    private final RentalFinTransactionMapper rentalFinTransactionMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<RentalFinTransactionDTO> findAll() {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can view all rental transactions");
        }
        return rentalFinTransactionMapper.toDTOList(rentalFinTransactionRepository.findAll());
    }

    @Transactional(readOnly = true)
    public RentalFinTransactionDTO findById(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can view rental transactions");
        }
        
        RentalFinTransaction transaction = rentalFinTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalFinTransaction", "id", id));
        
        return rentalFinTransactionMapper.toDTO(transaction);
    }

    @Transactional(readOnly = true)
    public RentalFinTransactionDTO findByExternalId(String externalId) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can view rental transactions");
        }
        
        RentalFinTransaction transaction = rentalFinTransactionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("RentalFinTransaction", "externalId", externalId));
        
        return rentalFinTransactionMapper.toDTO(transaction);
    }

    @Transactional
    public RentalFinTransactionDTO create(RentalFinTransactionDTO dto) {
        RentalFinTransaction transaction = rentalFinTransactionMapper.toEntity(dto);
        
        // External IDs are provided directly in the DTO
        log.debug("Creating rental transaction with rentalExternalId: {}", 
                dto.getRentalExternalId());
        
        RentalFinTransaction savedTransaction = rentalFinTransactionRepository.save(transaction);
        return rentalFinTransactionMapper.toDTO(savedTransaction);
    }

    @Transactional
    public RentalFinTransactionDTO update(Long id, RentalFinTransactionDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can update rental transactions");
        }
        
        RentalFinTransaction existingTransaction = rentalFinTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalFinTransaction", "id", id));
        
        existingTransaction.setRentalExternalId(dto.getRentalExternalId());
        
        RentalFinTransaction updatedTransaction = rentalFinTransactionRepository.save(existingTransaction);
        return rentalFinTransactionMapper.toDTO(updatedTransaction);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete rental transactions");
        }
        
        if (!rentalFinTransactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("RentalFinTransaction", "id", id);
        }
        rentalFinTransactionRepository.deleteById(id);
    }
}




