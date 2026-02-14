package org.clickenrent.supportservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeEngineErrorCodeDTO;
import org.clickenrent.supportservice.entity.BikeEngineErrorCode;
import org.clickenrent.supportservice.repository.ErrorCodeRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeEngineErrorCode entity and BikeEngineErrorCodeDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeEngineErrorCodeMapper {

    private final ErrorCodeRepository errorCodeRepository;

    public BikeEngineErrorCodeDTO toDto(BikeEngineErrorCode entity) {
        if (entity == null) {
            return null;
        }

        return BikeEngineErrorCodeDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .bikeEngineExternalId(entity.getBikeEngineExternalId())
                .errorCodeId(entity.getErrorCode() != null ? entity.getErrorCode().getId() : null)
                .errorCodeName(entity.getErrorCode() != null ? entity.getErrorCode().getName() : null)
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public BikeEngineErrorCode toEntity(BikeEngineErrorCodeDTO dto) {
        if (dto == null) {
            return null;
        }

        BikeEngineErrorCode.BikeEngineErrorCodeBuilder builder = BikeEngineErrorCode.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .bikeEngineExternalId(dto.getBikeEngineExternalId());

        if (dto.getErrorCodeId() != null) {
            builder.errorCode(errorCodeRepository.findById(dto.getErrorCodeId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(BikeEngineErrorCodeDTO dto, BikeEngineErrorCode entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getExternalId() != null) {
            entity.setExternalId(dto.getExternalId());
        }
        if (dto.getBikeEngineExternalId() != null) {
            entity.setBikeEngineExternalId(dto.getBikeEngineExternalId());
        }
        if (dto.getErrorCodeId() != null) {
            errorCodeRepository.findById(dto.getErrorCodeId()).ifPresent(entity::setErrorCode);
        }
    }
}
