package org.clickenrent.supportservice.mapper;

import org.clickenrent.supportservice.dto.BikeRentalFeedbackDTO;
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
                .userId(entity.getUserId())
                .bikeRentalId(entity.getBikeRentalId())
                .rate(entity.getRate())
                .comment(entity.getComment())
                .dateTime(entity.getDateTime())
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
                .userId(dto.getUserId())
                .bikeRentalId(dto.getBikeRentalId())
                .rate(dto.getRate())
                .comment(dto.getComment())
                .dateTime(dto.getDateTime())
                .build();
    }

    public void updateEntityFromDto(BikeRentalFeedbackDTO dto, BikeRentalFeedback entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getUserId() != null) {
            entity.setUserId(dto.getUserId());
        }
        if (dto.getBikeRentalId() != null) {
            entity.setBikeRentalId(dto.getBikeRentalId());
        }
        if (dto.getRate() != null) {
            entity.setRate(dto.getRate());
        }
        if (dto.getComment() != null) {
            entity.setComment(dto.getComment());
        }
        if (dto.getDateTime() != null) {
            entity.setDateTime(dto.getDateTime());
        }
    }
}
