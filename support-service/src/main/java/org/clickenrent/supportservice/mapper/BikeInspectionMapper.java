package org.clickenrent.supportservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionDTO;
import org.clickenrent.supportservice.entity.BikeInspection;
import org.clickenrent.supportservice.repository.BikeInspectionStatusRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeInspection entity and BikeInspectionDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeInspectionMapper {

    private final BikeInspectionStatusRepository bikeInspectionStatusRepository;

    public BikeInspectionDTO toDto(BikeInspection entity) {
        if (entity == null) {
            return null;
        }

        return BikeInspectionDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .userExternalId(entity.getUserExternalId())
                .companyExternalId(entity.getCompanyExternalId())
                .comment(entity.getComment())
                .bikeInspectionStatusId(entity.getBikeInspectionStatus() != null ? entity.getBikeInspectionStatus().getId() : null)
                .bikeInspectionStatusName(entity.getBikeInspectionStatus() != null ? entity.getBikeInspectionStatus().getName() : null)
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public BikeInspection toEntity(BikeInspectionDTO dto) {
        if (dto == null) {
            return null;
        }

        BikeInspection.BikeInspectionBuilder<?, ?> builder = BikeInspection.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .userExternalId(dto.getUserExternalId())
                .companyExternalId(dto.getCompanyExternalId())
                .comment(dto.getComment());

        if (dto.getBikeInspectionStatusId() != null) {
            builder.bikeInspectionStatus(bikeInspectionStatusRepository.findById(dto.getBikeInspectionStatusId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(BikeInspectionDTO dto, BikeInspection entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getUserExternalId() != null) {
            entity.setUserExternalId(dto.getUserExternalId());
        }
        if (dto.getCompanyExternalId() != null) {
            entity.setCompanyExternalId(dto.getCompanyExternalId());
        }
        if (dto.getComment() != null) {
            entity.setComment(dto.getComment());
        }
        if (dto.getBikeInspectionStatusId() != null) {
            bikeInspectionStatusRepository.findById(dto.getBikeInspectionStatusId()).ifPresent(entity::setBikeInspectionStatus);
        }
    }
}
