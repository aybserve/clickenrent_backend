package org.clickenrent.supportservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.SupportRequestGuideItemDTO;
import org.clickenrent.supportservice.entity.SupportRequestGuideItem;
import org.clickenrent.supportservice.repository.BikeIssueRepository;
import org.clickenrent.supportservice.repository.SupportRequestStatusRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between SupportRequestGuideItem entity and SupportRequestGuideItemDTO.
 */
@Component
@RequiredArgsConstructor
public class SupportRequestGuideItemMapper {

    private final BikeIssueRepository bikeIssueRepository;
    private final SupportRequestStatusRepository supportRequestStatusRepository;

    public SupportRequestGuideItemDTO toDto(SupportRequestGuideItem entity) {
        if (entity == null) {
            return null;
        }

        return SupportRequestGuideItemDTO.builder()
                .id(entity.getId())
                .itemIndex(entity.getItemIndex())
                .description(entity.getDescription())
                .bikeIssueId(entity.getBikeIssue() != null ? entity.getBikeIssue().getId() : null)
                .bikeIssueName(entity.getBikeIssue() != null ? entity.getBikeIssue().getName() : null)
                .supportRequestStatusId(entity.getSupportRequestStatus() != null ? entity.getSupportRequestStatus().getId() : null)
                .supportRequestStatusName(entity.getSupportRequestStatus() != null ? entity.getSupportRequestStatus().getName() : null)
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public SupportRequestGuideItem toEntity(SupportRequestGuideItemDTO dto) {
        if (dto == null) {
            return null;
        }

        SupportRequestGuideItem.SupportRequestGuideItemBuilder<?, ?> builder = SupportRequestGuideItem.builder()
                .id(dto.getId())
                .itemIndex(dto.getItemIndex())
                .description(dto.getDescription());

        if (dto.getBikeIssueId() != null) {
            builder.bikeIssue(bikeIssueRepository.findById(dto.getBikeIssueId()).orElse(null));
        }
        if (dto.getSupportRequestStatusId() != null) {
            builder.supportRequestStatus(supportRequestStatusRepository.findById(dto.getSupportRequestStatusId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(SupportRequestGuideItemDTO dto, SupportRequestGuideItem entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getItemIndex() != null) {
            entity.setItemIndex(dto.getItemIndex());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getBikeIssueId() != null) {
            bikeIssueRepository.findById(dto.getBikeIssueId()).ifPresent(entity::setBikeIssue);
        }
        if (dto.getSupportRequestStatusId() != null) {
            supportRequestStatusRepository.findById(dto.getSupportRequestStatusId()).ifPresent(entity::setSupportRequestStatus);
        }
    }
}

