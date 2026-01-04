package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.LockStatusDTO;
import org.clickenrent.rentalservice.entity.LockStatus;
import org.springframework.stereotype.Component;

@Component
public class LockStatusMapper {

    public LockStatusDTO toDto(LockStatus entity) {
        if (entity == null) return null;
        return LockStatusDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .name(entity.getName())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public LockStatus toEntity(LockStatusDTO dto) {
        if (dto == null) return null;
        return LockStatus.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(LockStatusDTO dto, LockStatus entity) {
        if (dto == null || entity == null) return;
        if (dto.getExternalId() != null) {
            entity.setExternalId(dto.getExternalId());
        }
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
    }
}








