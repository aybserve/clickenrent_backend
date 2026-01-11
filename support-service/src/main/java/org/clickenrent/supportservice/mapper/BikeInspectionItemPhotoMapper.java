package org.clickenrent.supportservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemPhotoDTO;
import org.clickenrent.supportservice.entity.BikeInspectionItemPhoto;
import org.clickenrent.supportservice.repository.BikeInspectionItemRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeInspectionItemPhoto entity and BikeInspectionItemPhotoDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeInspectionItemPhotoMapper {

    private final BikeInspectionItemRepository bikeInspectionItemRepository;

    public BikeInspectionItemPhotoDTO toDto(BikeInspectionItemPhoto entity) {
        if (entity == null) {
            return null;
        }

        return BikeInspectionItemPhotoDTO.builder()
                .id(entity.getId())
                .bikeInspectionItemId(entity.getBikeInspectionItem() != null ? entity.getBikeInspectionItem().getId() : null)
                .photoUrl(entity.getPhotoUrl())
                .companyExternalId(entity.getCompanyExternalId())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public BikeInspectionItemPhoto toEntity(BikeInspectionItemPhotoDTO dto) {
        if (dto == null) {
            return null;
        }

        BikeInspectionItemPhoto.BikeInspectionItemPhotoBuilder<?, ?> builder = BikeInspectionItemPhoto.builder()
                .id(dto.getId())
                .photoUrl(dto.getPhotoUrl())
                .companyExternalId(dto.getCompanyExternalId());

        if (dto.getBikeInspectionItemId() != null) {
            builder.bikeInspectionItem(bikeInspectionItemRepository.findById(dto.getBikeInspectionItemId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(BikeInspectionItemPhotoDTO dto, BikeInspectionItemPhoto entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getBikeInspectionItemId() != null) {
            bikeInspectionItemRepository.findById(dto.getBikeInspectionItemId()).ifPresent(entity::setBikeInspectionItem);
        }
        if (dto.getPhotoUrl() != null) {
            entity.setPhotoUrl(dto.getPhotoUrl());
        }
        if (dto.getCompanyExternalId() != null) {
            entity.setCompanyExternalId(dto.getCompanyExternalId());
        }
    }
}
