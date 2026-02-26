package org.clickenrent.paymentservice.mapper;

import org.clickenrent.paymentservice.dto.LocationBankAccountDTO;
import org.clickenrent.paymentservice.entity.LocationBankAccount;
import org.springframework.stereotype.Component;

@Component
public class LocationBankAccountMapper {
    
    public LocationBankAccountDTO toDTO(LocationBankAccount entity) {
        if (entity == null) {
            return null;
        }
        
        return LocationBankAccountDTO.builder()
            .externalId(entity.getExternalId())
            .companyExternalId(entity.getCompanyExternalId())
            .locationExternalId(entity.getLocationExternalId())
            .accountHolderName(entity.getAccountHolderName())
            .iban(entity.getIban())
            .bic(entity.getBic())
            .currency(entity.getCurrency())
            .isVerified(entity.getIsVerified())
            .isActive(entity.getIsActive())
            .verificationNotes(entity.getVerificationNotes())
            .createdAt(entity.getDateCreated())
            .updatedAt(entity.getLastDateModified())
            .createdBy(entity.getCreatedBy())
            .updatedBy(entity.getLastModifiedBy())
            .build();
    }
    
    public LocationBankAccount toEntity(LocationBankAccountDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return LocationBankAccount.builder()
            .externalId(dto.getExternalId())
            .companyExternalId(dto.getCompanyExternalId())
            .locationExternalId(dto.getLocationExternalId())
            .accountHolderName(dto.getAccountHolderName())
            .iban(dto.getIban())
            .bic(dto.getBic())
            .currency(dto.getCurrency())
            .isVerified(dto.getIsVerified())
            .isActive(dto.getIsActive())
            .verificationNotes(dto.getVerificationNotes())
            .build();
    }
    
    public void updateEntityFromDTO(LocationBankAccountDTO dto, LocationBankAccount entity) {
        if (dto == null || entity == null) {
            return;
        }
        
        if (dto.getAccountHolderName() != null) {
            entity.setAccountHolderName(dto.getAccountHolderName());
        }
        if (dto.getIban() != null) {
            entity.setIban(dto.getIban());
        }
        if (dto.getBic() != null) {
            entity.setBic(dto.getBic());
        }
        if (dto.getCurrency() != null) {
            entity.setCurrency(dto.getCurrency());
        }
        if (dto.getIsVerified() != null) {
            entity.setIsVerified(dto.getIsVerified());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
        if (dto.getVerificationNotes() != null) {
            entity.setVerificationNotes(dto.getVerificationNotes());
        }
    }
}
