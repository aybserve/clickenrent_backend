package org.clickenrent.supportservice.mapper;

import org.clickenrent.supportservice.dto.BikeUnitDTO;
import org.clickenrent.supportservice.entity.BikeUnit;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeUnit entity and BikeUnitDTO.
 */
@Component
public class BikeUnitMapper {

    public BikeUnitDTO toDto(BikeUnit entity) {
        if (entity == null) {
            return null;
        }

        return BikeUnitDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .name(entity.getName())
                .companyExternalId(entity.getCompanyExternalId())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public BikeUnit toEntity(BikeUnitDTO dto) {
        if (dto == null) {
            return null;
        }

        return BikeUnit.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .companyExternalId(dto.getCompanyExternalId())
                .build();
    }

    public void updateEntityFromDto(BikeUnitDTO dto, BikeUnit entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getCompanyExternalId() != null) {
            entity.setCompanyExternalId(dto.getCompanyExternalId());
        }
    }
}
