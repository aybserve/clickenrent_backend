package org.clickenrent.supportservice.mapper;

import org.clickenrent.supportservice.dto.ErrorCodeDTO;
import org.clickenrent.supportservice.entity.ErrorCode;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between ErrorCode entity and ErrorCodeDTO.
 */
@Component
public class ErrorCodeMapper {

    public ErrorCodeDTO toDto(ErrorCode entity) {
        if (entity == null) {
            return null;
        }

        return ErrorCodeDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .name(entity.getName())
                .bikeEngineExternalId(entity.getBikeEngineExternalId())
                .description(entity.getDescription())
                .commonCause(entity.getCommonCause())
                .diagnosticSteps(entity.getDiagnosticSteps())
                .recommendedFix(entity.getRecommendedFix())
                .notes(entity.getNotes())
                .isFixableByClient(entity.getIsFixableByClient())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public ErrorCode toEntity(ErrorCodeDTO dto) {
        if (dto == null) {
            return null;
        }

        return ErrorCode.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .bikeEngineExternalId(dto.getBikeEngineExternalId())
                .description(dto.getDescription())
                .commonCause(dto.getCommonCause())
                .diagnosticSteps(dto.getDiagnosticSteps())
                .recommendedFix(dto.getRecommendedFix())
                .notes(dto.getNotes())
                .isFixableByClient(dto.getIsFixableByClient())
                .build();
    }

    public void updateEntityFromDto(ErrorCodeDTO dto, ErrorCode entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getBikeEngineExternalId() != null) {
            entity.setBikeEngineExternalId(dto.getBikeEngineExternalId());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getCommonCause() != null) {
            entity.setCommonCause(dto.getCommonCause());
        }
        if (dto.getDiagnosticSteps() != null) {
            entity.setDiagnosticSteps(dto.getDiagnosticSteps());
        }
        if (dto.getRecommendedFix() != null) {
            entity.setRecommendedFix(dto.getRecommendedFix());
        }
        if (dto.getNotes() != null) {
            entity.setNotes(dto.getNotes());
        }
        if (dto.getIsFixableByClient() != null) {
            entity.setIsFixableByClient(dto.getIsFixableByClient());
        }
    }
}


