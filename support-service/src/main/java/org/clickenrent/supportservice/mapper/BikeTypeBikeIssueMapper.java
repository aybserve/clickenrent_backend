package org.clickenrent.supportservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeTypeBikeIssueDTO;
import org.clickenrent.supportservice.entity.BikeTypeBikeIssue;
import org.clickenrent.supportservice.repository.BikeIssueRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeTypeBikeIssue entity and BikeTypeBikeIssueDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeTypeBikeIssueMapper {

    private final BikeIssueRepository bikeIssueRepository;

    public BikeTypeBikeIssueDTO toDto(BikeTypeBikeIssue entity) {
        if (entity == null) {
            return null;
        }

        return BikeTypeBikeIssueDTO.builder()
                .id(entity.getId())
                .bikeTypeId(entity.getBikeTypeId())
                .bikeIssueId(entity.getBikeIssue() != null ? entity.getBikeIssue().getId() : null)
                .bikeIssueName(entity.getBikeIssue() != null ? entity.getBikeIssue().getName() : null)
                .build();
    }

    public BikeTypeBikeIssue toEntity(BikeTypeBikeIssueDTO dto) {
        if (dto == null) {
            return null;
        }

        BikeTypeBikeIssue.BikeTypeBikeIssueBuilder builder = BikeTypeBikeIssue.builder()
                .id(dto.getId())
                .bikeTypeId(dto.getBikeTypeId());

        if (dto.getBikeIssueId() != null) {
            builder.bikeIssue(bikeIssueRepository.findById(dto.getBikeIssueId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(BikeTypeBikeIssueDTO dto, BikeTypeBikeIssue entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getBikeTypeId() != null) {
            entity.setBikeTypeId(dto.getBikeTypeId());
        }
        if (dto.getBikeIssueId() != null) {
            bikeIssueRepository.findById(dto.getBikeIssueId()).ifPresent(entity::setBikeIssue);
        }
    }
}


