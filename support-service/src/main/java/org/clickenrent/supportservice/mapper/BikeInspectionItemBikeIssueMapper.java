package org.clickenrent.supportservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemBikeIssueDTO;
import org.clickenrent.supportservice.entity.BikeInspectionItemBikeIssue;
import org.clickenrent.supportservice.repository.BikeInspectionItemRepository;
import org.clickenrent.supportservice.repository.BikeIssueRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeInspectionItemBikeIssue entity and BikeInspectionItemBikeIssueDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeInspectionItemBikeIssueMapper {

    private final BikeInspectionItemRepository bikeInspectionItemRepository;
    private final BikeIssueRepository bikeIssueRepository;

    public BikeInspectionItemBikeIssueDTO toDto(BikeInspectionItemBikeIssue entity) {
        if (entity == null) {
            return null;
        }

        return BikeInspectionItemBikeIssueDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .bikeInspectionItemId(entity.getBikeInspectionItem() != null ? entity.getBikeInspectionItem().getId() : null)
                .bikeIssueId(entity.getBikeIssue() != null ? entity.getBikeIssue().getId() : null)
                .bikeIssueName(entity.getBikeIssue() != null ? entity.getBikeIssue().getName() : null)
                .companyExternalId(entity.getCompanyExternalId())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public BikeInspectionItemBikeIssue toEntity(BikeInspectionItemBikeIssueDTO dto) {
        if (dto == null) {
            return null;
        }

        BikeInspectionItemBikeIssue.BikeInspectionItemBikeIssueBuilder builder = BikeInspectionItemBikeIssue.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .companyExternalId(dto.getCompanyExternalId());

        if (dto.getBikeInspectionItemId() != null) {
            builder.bikeInspectionItem(bikeInspectionItemRepository.findById(dto.getBikeInspectionItemId()).orElse(null));
        }
        if (dto.getBikeIssueId() != null) {
            builder.bikeIssue(bikeIssueRepository.findById(dto.getBikeIssueId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(BikeInspectionItemBikeIssueDTO dto, BikeInspectionItemBikeIssue entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getBikeInspectionItemId() != null) {
            bikeInspectionItemRepository.findById(dto.getBikeInspectionItemId()).ifPresent(entity::setBikeInspectionItem);
        }
        if (dto.getBikeIssueId() != null) {
            bikeIssueRepository.findById(dto.getBikeIssueId()).ifPresent(entity::setBikeIssue);
        }
        if (dto.getCompanyExternalId() != null) {
            entity.setCompanyExternalId(dto.getCompanyExternalId());
        }
    }
}
