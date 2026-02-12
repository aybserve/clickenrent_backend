package org.clickenrent.supportservice.mapper;

import org.clickenrent.contracts.support.BikeRentalFeedbackDTO;
import org.clickenrent.supportservice.entity.BikeRentalFeedback;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeRentalFeedback entity and BikeRentalFeedbackDTO.
 */
@Component
public class BikeRentalFeedbackMapper {

    public BikeRentalFeedbackDTO toDto(BikeRentalFeedback entity) {
        if (entity == null) {
            return null;
        }

        return BikeRentalFeedbackDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .userExternalId(entity.getUserExternalId())
                .bikeRentalExternalId(entity.getBikeRentalExternalId())
                .rate(entity.getRate())
                .comment(entity.getComment())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public BikeRentalFeedback toEntity(BikeRentalFeedbackDTO dto) {
        if (dto == null) {
            return null;
        }

        return BikeRentalFeedback.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .userExternalId(dto.getUserExternalId())
                .bikeRentalExternalId(dto.getBikeRentalExternalId())
                .rate(dto.getRate())
                .comment(dto.getComment())
                .build();
    }

    public void updateEntityFromDto(BikeRentalFeedbackDTO dto, BikeRentalFeedback entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getUserExternalId() != null) {
            entity.setUserExternalId(dto.getUserExternalId());
        }
        if (dto.getBikeRentalExternalId() != null) {
            entity.setBikeRentalExternalId(dto.getBikeRentalExternalId());
        }
        if (dto.getRate() != null) {
            entity.setRate(dto.getRate());
        }
        if (dto.getComment() != null) {
            entity.setComment(dto.getComment());
        }
    }
}








