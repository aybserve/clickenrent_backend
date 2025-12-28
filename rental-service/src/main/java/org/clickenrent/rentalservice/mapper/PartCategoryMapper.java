package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.PartCategoryDTO;
import org.clickenrent.rentalservice.entity.PartCategory;
import org.clickenrent.rentalservice.repository.PartCategoryRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between PartCategory entity and PartCategoryDTO.
 * Handles hierarchical parent-child relationships.
 */
@Component
@RequiredArgsConstructor
public class PartCategoryMapper {

    private final PartCategoryRepository partCategoryRepository;

    public PartCategoryDTO toDto(PartCategory partCategory) {
        if (partCategory == null) {
            return null;
        }

        return PartCategoryDTO.builder()
                .id(partCategory.getId())
                .externalId(partCategory.getExternalId())
                .name(partCategory.getName())
                .parentCategoryId(partCategory.getParentCategory() != null ? partCategory.getParentCategory().getId() : null)
                .build();
    }

    public PartCategory toEntity(PartCategoryDTO dto) {
        if (dto == null) {
            return null;
        }

        PartCategory.PartCategoryBuilder builder = PartCategory.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName());

        if (dto.getParentCategoryId() != null) {
            builder.parentCategory(partCategoryRepository.findById(dto.getParentCategoryId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(PartCategoryDTO dto, PartCategory partCategory) {
        if (dto == null || partCategory == null) {
            return;
        }

        if (dto.getName() != null) {
            partCategory.setName(dto.getName());
        }
    }
}








