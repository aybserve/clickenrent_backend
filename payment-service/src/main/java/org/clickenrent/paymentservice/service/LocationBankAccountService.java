package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.dto.LocationBankAccountDTO;
import org.clickenrent.paymentservice.entity.LocationBankAccount;
import org.clickenrent.paymentservice.mapper.LocationBankAccountMapper;
import org.clickenrent.paymentservice.repository.LocationBankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationBankAccountService {
    
    private final LocationBankAccountRepository locationBankAccountRepository;
    private final LocationBankAccountMapper locationBankAccountMapper;
    
    @Transactional
    public LocationBankAccountDTO createLocationBankAccount(LocationBankAccountDTO dto) {
        log.info("Creating location bank account for location: {}", dto.getLocationExternalId());
        
        // Check if bank account already exists for this location
        if (locationBankAccountRepository.existsByLocationExternalId(dto.getLocationExternalId())) {
            throw new IllegalStateException("Bank account already exists for location: " + dto.getLocationExternalId());
        }
        
        LocationBankAccount entity = locationBankAccountMapper.toEntity(dto);
        entity.sanitizeForCreate();
        LocationBankAccount saved = locationBankAccountRepository.save(entity);
        
        log.info("Created location bank account with external ID: {}", saved.getExternalId());
        return locationBankAccountMapper.toDTO(saved);
    }
    
    @Transactional(readOnly = true)
    public LocationBankAccountDTO getByLocationExternalId(String locationExternalId) {
        log.debug("Fetching bank account for location: {}", locationExternalId);
        
        return locationBankAccountRepository.findByLocationExternalId(locationExternalId)
            .map(locationBankAccountMapper::toDTO)
            .orElse(null);
    }
    
    @Transactional(readOnly = true)
    public LocationBankAccountDTO getByExternalId(String externalId) {
        log.debug("Fetching bank account by external ID: {}", externalId);
        
        return locationBankAccountRepository.findByExternalId(externalId)
            .map(locationBankAccountMapper::toDTO)
            .orElse(null);
    }
    
    @Transactional(readOnly = true)
    public List<LocationBankAccountDTO> getAllByCompanyExternalId(String companyExternalId) {
        log.debug("Fetching all bank accounts for company: {}", companyExternalId);
        
        return locationBankAccountRepository.findByCompanyExternalId(companyExternalId)
            .stream()
            .map(locationBankAccountMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public LocationBankAccountDTO updateLocationBankAccount(String externalId, LocationBankAccountDTO dto) {
        log.info("Updating location bank account: {}", externalId);
        
        LocationBankAccount entity = locationBankAccountRepository.findByExternalId(externalId)
            .orElseThrow(() -> new IllegalArgumentException("Bank account not found: " + externalId));
        
        locationBankAccountMapper.updateEntityFromDTO(dto, entity);
        LocationBankAccount updated = locationBankAccountRepository.save(entity);
        
        log.info("Updated location bank account: {}", externalId);
        return locationBankAccountMapper.toDTO(updated);
    }
    
    @Transactional
    public void deactivateLocationBankAccount(String externalId) {
        log.info("Deactivating location bank account: {}", externalId);
        
        LocationBankAccount entity = locationBankAccountRepository.findByExternalId(externalId)
            .orElseThrow(() -> new IllegalArgumentException("Bank account not found: " + externalId));
        
        entity.setIsActive(false);
        locationBankAccountRepository.save(entity);
        
        log.info("Deactivated location bank account: {}", externalId);
    }
    
    @Transactional
    public LocationBankAccountDTO verifyLocationBankAccount(String externalId, String notes) {
        log.info("Verifying location bank account: {}", externalId);
        
        LocationBankAccount entity = locationBankAccountRepository.findByExternalId(externalId)
            .orElseThrow(() -> new IllegalArgumentException("Bank account not found: " + externalId));
        
        entity.setIsVerified(true);
        entity.setVerificationNotes(notes);
        LocationBankAccount updated = locationBankAccountRepository.save(entity);
        
        log.info("Verified location bank account: {}", externalId);
        return locationBankAccountMapper.toDTO(updated);
    }
}
