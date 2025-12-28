package org.clickenrent.supportservice.mapper;

import org.clickenrent.supportservice.dto.FeedbackDTO;
import org.clickenrent.supportservice.entity.Feedback;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Feedback entity and FeedbackDTO.
 */
@Component
public class FeedbackMapper {

    public FeedbackDTO toDto(Feedback entity) {
        if (entity == null) {
            return null;
        }

        return FeedbackDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .userExternalId(entity.getUserExternalId())
                .rate(entity.getRate())
                .comment(entity.getComment())
                .dateTime(entity.getDateTime())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public Feedback toEntity(FeedbackDTO dto) {
        if (dto == null) {
            return null;
        }

        return Feedback.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .userExternalId(dto.getUserExternalId())
                .rate(dto.getRate())
                .comment(dto.getComment())
                .dateTime(dto.getDateTime())
                .build();
    }

    public void updateEntityFromDto(FeedbackDTO dto, Feedback entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getUserExternalId() != null) {
            entity.setUserExternalId(dto.getUserExternalId());
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








