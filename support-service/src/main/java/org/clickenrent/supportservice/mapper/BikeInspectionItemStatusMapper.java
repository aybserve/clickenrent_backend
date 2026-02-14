package org.clickenrent.supportservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemStatusDTO;
import org.clickenrent.supportservice.entity.BikeInspectionItemStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeInspectionItemStatus entity and BikeInspectionItemStatusDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeInspectionItemStatusMapper {

    public BikeInspectionItemStatusDTO toDto(BikeInspectionItemStatus entity) {
        if (entity == null) {
            return null;
        }

        return BikeInspectionItemStatusDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .name(entity.getName())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public BikeInspectionItemStatus toEntity(BikeInspectionItemStatusDTO dto) {
        if (dto == null) {
            return null;
        }

        return BikeInspectionItemStatus.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(BikeInspectionItemStatusDTO dto, BikeInspectionItemStatus entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
    }
}
