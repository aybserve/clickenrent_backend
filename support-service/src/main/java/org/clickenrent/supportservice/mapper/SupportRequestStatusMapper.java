package org.clickenrent.supportservice.mapper;

import org.clickenrent.supportservice.dto.SupportRequestStatusDTO;
import org.clickenrent.supportservice.entity.SupportRequestStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between SupportRequestStatus entity and SupportRequestStatusDTO.
 */
@Component
public class SupportRequestStatusMapper {

    public SupportRequestStatusDTO toDto(SupportRequestStatus entity) {
        if (entity == null) {
            return null;
        }

        return SupportRequestStatusDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public SupportRequestStatus toEntity(SupportRequestStatusDTO dto) {
        if (dto == null) {
            return null;
        }

        return SupportRequestStatus.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(SupportRequestStatusDTO dto, SupportRequestStatus entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
    }
}







