package org.clickenrent.supportservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionStatusDTO;
import org.clickenrent.supportservice.entity.BikeInspectionStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeInspectionStatus entity and BikeInspectionStatusDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeInspectionStatusMapper {

    public BikeInspectionStatusDTO toDto(BikeInspectionStatus entity) {
        if (entity == null) {
            return null;
        }

        return BikeInspectionStatusDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .name(entity.getName())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public BikeInspectionStatus toEntity(BikeInspectionStatusDTO dto) {
        if (dto == null) {
            return null;
        }

        return BikeInspectionStatus.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(BikeInspectionStatusDTO dto, BikeInspectionStatus entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
    }
}
