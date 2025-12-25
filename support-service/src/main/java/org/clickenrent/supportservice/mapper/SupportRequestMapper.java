package org.clickenrent.supportservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.SupportRequestDTO;
import org.clickenrent.supportservice.entity.SupportRequest;
import org.clickenrent.supportservice.repository.ErrorCodeRepository;
import org.clickenrent.supportservice.repository.SupportRequestStatusRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between SupportRequest entity and SupportRequestDTO.
 */
@Component
@RequiredArgsConstructor
public class SupportRequestMapper {

    private final ErrorCodeRepository errorCodeRepository;
    private final SupportRequestStatusRepository supportRequestStatusRepository;

    public SupportRequestDTO toDto(SupportRequest entity) {
        if (entity == null) {
            return null;
        }

        return SupportRequestDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .userExternalId(entity.getUserExternalId())
                .bikeExternalId(entity.getBikeExternalId())
                .isNearLocation(entity.getIsNearLocation())
                .photoUrl(entity.getPhotoUrl())
                .errorCodeId(entity.getErrorCode() != null ? entity.getErrorCode().getId() : null)
                .errorCodeName(entity.getErrorCode() != null ? entity.getErrorCode().getName() : null)
                .supportRequestStatusId(entity.getSupportRequestStatus() != null ? entity.getSupportRequestStatus().getId() : null)
                .supportRequestStatusName(entity.getSupportRequestStatus() != null ? entity.getSupportRequestStatus().getName() : null)
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public SupportRequest toEntity(SupportRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        SupportRequest.SupportRequestBuilder<?, ?> builder = SupportRequest.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .userExternalId(dto.getUserExternalId())
                .bikeExternalId(dto.getBikeExternalId())
                .isNearLocation(dto.getIsNearLocation())
                .photoUrl(dto.getPhotoUrl());

        if (dto.getErrorCodeId() != null) {
            builder.errorCode(errorCodeRepository.findById(dto.getErrorCodeId()).orElse(null));
        }
        if (dto.getSupportRequestStatusId() != null) {
            builder.supportRequestStatus(supportRequestStatusRepository.findById(dto.getSupportRequestStatusId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(SupportRequestDTO dto, SupportRequest entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getUserExternalId() != null) {
            entity.setUserExternalId(dto.getUserExternalId());
        }
        if (dto.getBikeExternalId() != null) {
            entity.setBikeExternalId(dto.getBikeExternalId());
        }
        if (dto.getIsNearLocation() != null) {
            entity.setIsNearLocation(dto.getIsNearLocation());
        }
        if (dto.getPhotoUrl() != null) {
            entity.setPhotoUrl(dto.getPhotoUrl());
        }
        if (dto.getErrorCodeId() != null) {
            errorCodeRepository.findById(dto.getErrorCodeId()).ifPresent(entity::setErrorCode);
        }
        if (dto.getSupportRequestStatusId() != null) {
            supportRequestStatusRepository.findById(dto.getSupportRequestStatusId()).ifPresent(entity::setSupportRequestStatus);
        }
    }
}






