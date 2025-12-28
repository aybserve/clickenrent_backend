package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.LockProviderDTO;
import org.clickenrent.rentalservice.entity.LockProvider;
import org.springframework.stereotype.Component;

@Component
public class LockProviderMapper {

    public LockProviderDTO toDto(LockProvider entity) {
        if (entity == null) return null;
        return LockProviderDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .apiEndpoint(entity.getApiEndpoint())
                .apiKey(entity.getApiKey())
                .encryptionKey(entity.getEncryptionKey())
                .isActive(entity.getIsActive())
                .build();
    }

    public LockProvider toEntity(LockProviderDTO dto) {
        if (dto == null) return null;
        return LockProvider.builder()
                .id(dto.getId())
                .name(dto.getName())
                .apiEndpoint(dto.getApiEndpoint())
                .apiKey(dto.getApiKey())
                .encryptionKey(dto.getEncryptionKey())
                .isActive(dto.getIsActive())
                .build();
    }

    public void updateEntityFromDto(LockProviderDTO dto, LockProvider entity) {
        if (dto == null || entity == null) return;
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getApiEndpoint() != null) {
            entity.setApiEndpoint(dto.getApiEndpoint());
        }
        if (dto.getApiKey() != null) {
            entity.setApiKey(dto.getApiKey());
        }
        if (dto.getEncryptionKey() != null) {
            entity.setEncryptionKey(dto.getEncryptionKey());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
    }
}








