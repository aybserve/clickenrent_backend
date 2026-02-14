package org.clickenrent.supportservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemBikeUnitDTO;
import org.clickenrent.supportservice.entity.BikeInspectionItemBikeUnit;
import org.clickenrent.supportservice.repository.BikeInspectionItemRepository;
import org.clickenrent.supportservice.repository.BikeUnitRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeInspectionItemBikeUnit entity and BikeInspectionItemBikeUnitDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeInspectionItemBikeUnitMapper {

    private final BikeInspectionItemRepository bikeInspectionItemRepository;
    private final BikeUnitRepository bikeUnitRepository;

    public BikeInspectionItemBikeUnitDTO toDto(BikeInspectionItemBikeUnit entity) {
        if (entity == null) {
            return null;
        }

        return BikeInspectionItemBikeUnitDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .bikeInspectionItemId(entity.getBikeInspectionItem() != null ? entity.getBikeInspectionItem().getId() : null)
                .bikeUnitId(entity.getBikeUnit() != null ? entity.getBikeUnit().getId() : null)
                .bikeUnitName(entity.getBikeUnit() != null ? entity.getBikeUnit().getName() : null)
                .hasProblem(entity.getHasProblem())
                .companyExternalId(entity.getCompanyExternalId())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public BikeInspectionItemBikeUnit toEntity(BikeInspectionItemBikeUnitDTO dto) {
        if (dto == null) {
            return null;
        }

        BikeInspectionItemBikeUnit.BikeInspectionItemBikeUnitBuilder builder = BikeInspectionItemBikeUnit.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .hasProblem(dto.getHasProblem())
                .companyExternalId(dto.getCompanyExternalId());

        if (dto.getBikeInspectionItemId() != null) {
            builder.bikeInspectionItem(bikeInspectionItemRepository.findById(dto.getBikeInspectionItemId()).orElse(null));
        }
        if (dto.getBikeUnitId() != null) {
            builder.bikeUnit(bikeUnitRepository.findById(dto.getBikeUnitId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(BikeInspectionItemBikeUnitDTO dto, BikeInspectionItemBikeUnit entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getBikeInspectionItemId() != null) {
            bikeInspectionItemRepository.findById(dto.getBikeInspectionItemId()).ifPresent(entity::setBikeInspectionItem);
        }
        if (dto.getBikeUnitId() != null) {
            bikeUnitRepository.findById(dto.getBikeUnitId()).ifPresent(entity::setBikeUnit);
        }
        if (dto.getHasProblem() != null) {
            entity.setHasProblem(dto.getHasProblem());
        }
        if (dto.getCompanyExternalId() != null) {
            entity.setCompanyExternalId(dto.getCompanyExternalId());
        }
    }
}
