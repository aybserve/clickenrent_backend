package org.clickenrent.supportservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.SupportRequestBikeIssueDTO;
import org.clickenrent.supportservice.entity.SupportRequestBikeIssue;
import org.clickenrent.supportservice.repository.BikeIssueRepository;
import org.clickenrent.supportservice.repository.SupportRequestRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between SupportRequestBikeIssue entity and SupportRequestBikeIssueDTO.
 */
@Component
@RequiredArgsConstructor
public class SupportRequestBikeIssueMapper {

    private final SupportRequestRepository supportRequestRepository;
    private final BikeIssueRepository bikeIssueRepository;

    public SupportRequestBikeIssueDTO toDto(SupportRequestBikeIssue entity) {
        if (entity == null) {
            return null;
        }

        return SupportRequestBikeIssueDTO.builder()
                .id(entity.getId())
                .supportRequestId(entity.getSupportRequest() != null ? entity.getSupportRequest().getId() : null)
                .supportRequestExternalId(entity.getSupportRequest() != null ? entity.getSupportRequest().getExternalId() : null)
                .bikeIssueId(entity.getBikeIssue() != null ? entity.getBikeIssue().getId() : null)
                .bikeIssueName(entity.getBikeIssue() != null ? entity.getBikeIssue().getName() : null)
                .build();
    }

    public SupportRequestBikeIssue toEntity(SupportRequestBikeIssueDTO dto) {
        if (dto == null) {
            return null;
        }

        SupportRequestBikeIssue.SupportRequestBikeIssueBuilder builder = SupportRequestBikeIssue.builder()
                .id(dto.getId());

        if (dto.getSupportRequestId() != null) {
            builder.supportRequest(supportRequestRepository.findById(dto.getSupportRequestId()).orElse(null));
        }
        if (dto.getBikeIssueId() != null) {
            builder.bikeIssue(bikeIssueRepository.findById(dto.getBikeIssueId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(SupportRequestBikeIssueDTO dto, SupportRequestBikeIssue entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getSupportRequestId() != null) {
            supportRequestRepository.findById(dto.getSupportRequestId()).ifPresent(entity::setSupportRequest);
        }
        if (dto.getBikeIssueId() != null) {
            bikeIssueRepository.findById(dto.getBikeIssueId()).ifPresent(entity::setBikeIssue);
        }
    }
}

