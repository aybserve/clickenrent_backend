package org.clickenrent.supportservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemDTO;
import org.clickenrent.supportservice.entity.BikeInspectionItem;
import org.clickenrent.supportservice.repository.BikeInspectionItemStatusRepository;
import org.clickenrent.supportservice.repository.BikeInspectionRepository;
import org.clickenrent.supportservice.repository.ErrorCodeRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeInspectionItem entity and BikeInspectionItemDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeInspectionItemMapper {

    private final BikeInspectionRepository bikeInspectionRepository;
    private final BikeInspectionItemStatusRepository bikeInspectionItemStatusRepository;
    private final ErrorCodeRepository errorCodeRepository;

    public BikeInspectionItemDTO toDto(BikeInspectionItem entity) {
        if (entity == null) {
            return null;
        }

        return BikeInspectionItemDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .bikeInspectionId(entity.getBikeInspection() != null ? entity.getBikeInspection().getId() : null)
                .bikeExternalId(entity.getBikeExternalId())
                .companyExternalId(entity.getCompanyExternalId())
                .comment(entity.getComment())
                .bikeInspectionItemStatusId(entity.getBikeInspectionItemStatus() != null ? entity.getBikeInspectionItemStatus().getId() : null)
                .bikeInspectionItemStatusName(entity.getBikeInspectionItemStatus() != null ? entity.getBikeInspectionItemStatus().getName() : null)
                .errorCodeId(entity.getErrorCode() != null ? entity.getErrorCode().getId() : null)
                .errorCodeName(entity.getErrorCode() != null ? entity.getErrorCode().getName() : null)
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public BikeInspectionItem toEntity(BikeInspectionItemDTO dto) {
        if (dto == null) {
            return null;
        }

        BikeInspectionItem.BikeInspectionItemBuilder<?, ?> builder = BikeInspectionItem.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .bikeExternalId(dto.getBikeExternalId())
                .companyExternalId(dto.getCompanyExternalId())
                .comment(dto.getComment());

        if (dto.getBikeInspectionId() != null) {
            builder.bikeInspection(bikeInspectionRepository.findById(dto.getBikeInspectionId()).orElse(null));
        }
        if (dto.getBikeInspectionItemStatusId() != null) {
            builder.bikeInspectionItemStatus(bikeInspectionItemStatusRepository.findById(dto.getBikeInspectionItemStatusId()).orElse(null));
        }
        if (dto.getErrorCodeId() != null) {
            builder.errorCode(errorCodeRepository.findById(dto.getErrorCodeId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(BikeInspectionItemDTO dto, BikeInspectionItem entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getBikeInspectionId() != null) {
            bikeInspectionRepository.findById(dto.getBikeInspectionId()).ifPresent(entity::setBikeInspection);
        }
        if (dto.getBikeExternalId() != null) {
            entity.setBikeExternalId(dto.getBikeExternalId());
        }
        if (dto.getCompanyExternalId() != null) {
            entity.setCompanyExternalId(dto.getCompanyExternalId());
        }
        if (dto.getComment() != null) {
            entity.setComment(dto.getComment());
        }
        if (dto.getBikeInspectionItemStatusId() != null) {
            bikeInspectionItemStatusRepository.findById(dto.getBikeInspectionItemStatusId()).ifPresent(entity::setBikeInspectionItemStatus);
        }
        if (dto.getErrorCodeId() != null) {
            errorCodeRepository.findById(dto.getErrorCodeId()).ifPresent(entity::setErrorCode);
        }
    }
}
