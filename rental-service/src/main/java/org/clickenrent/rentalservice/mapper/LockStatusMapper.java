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
                .name(entity.getName())
                .build();
    }

    public LockStatus toEntity(LockStatusDTO dto) {
        if (dto == null) return null;
        return LockStatus.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(LockStatusDTO dto, LockStatus entity) {
        if (dto == null || entity == null) return;
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
    }
}
