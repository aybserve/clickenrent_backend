package org.clickenrent.supportservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeIssueDTO;
import org.clickenrent.supportservice.entity.BikeIssue;
import org.clickenrent.supportservice.repository.BikeIssueRepository;
import org.clickenrent.supportservice.repository.ResponsiblePersonRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeIssue entity and BikeIssueDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeIssueMapper {

    private final BikeIssueRepository bikeIssueRepository;
    private final ResponsiblePersonRepository responsiblePersonRepository;

    public BikeIssueDTO toDto(BikeIssue entity) {
        if (entity == null) {
            return null;
        }

        return BikeIssueDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .name(entity.getName())
                .description(entity.getDescription())
                .parentBikeIssueId(entity.getParentBikeIssue() != null ? entity.getParentBikeIssue().getId() : null)
                .isFixableByClient(entity.getIsFixableByClient())
                .responsiblePersonId(entity.getResponsiblePerson() != null ? entity.getResponsiblePerson().getId() : null)
                .responsiblePersonName(entity.getResponsiblePerson() != null ? entity.getResponsiblePerson().getName() : null)
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public BikeIssue toEntity(BikeIssueDTO dto) {
        if (dto == null) {
            return null;
        }

        BikeIssue.BikeIssueBuilder<?, ?> builder = BikeIssue.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .description(dto.getDescription())
                .isFixableByClient(dto.getIsFixableByClient());

        if (dto.getParentBikeIssueId() != null) {
            builder.parentBikeIssue(bikeIssueRepository.findById(dto.getParentBikeIssueId()).orElse(null));
        }
        if (dto.getResponsiblePersonId() != null) {
            builder.responsiblePerson(responsiblePersonRepository.findById(dto.getResponsiblePersonId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(BikeIssueDTO dto, BikeIssue entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getIsFixableByClient() != null) {
            entity.setIsFixableByClient(dto.getIsFixableByClient());
        }
        if (dto.getParentBikeIssueId() != null) {
            bikeIssueRepository.findById(dto.getParentBikeIssueId()).ifPresent(entity::setParentBikeIssue);
        }
        if (dto.getResponsiblePersonId() != null) {
            responsiblePersonRepository.findById(dto.getResponsiblePersonId()).ifPresent(entity::setResponsiblePerson);
        }
    }
}








