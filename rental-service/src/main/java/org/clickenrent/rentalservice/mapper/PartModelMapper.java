package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.PartModelDTO;
import org.clickenrent.rentalservice.entity.PartModel;
import org.clickenrent.rentalservice.repository.PartBrandRepository;
import org.clickenrent.rentalservice.repository.PartCategoryRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between PartModel entity and PartModelDTO.
 */
@Component
@RequiredArgsConstructor
public class PartModelMapper {

    private final PartBrandRepository partBrandRepository;
    private final PartCategoryRepository partCategoryRepository;

    public PartModelDTO toDto(PartModel partModel) {
        if (partModel == null) {
            return null;
        }

        return PartModelDTO.builder()
                .id(partModel.getId())
                .externalId(partModel.getExternalId())
                .name(partModel.getName())
                .partBrandId(partModel.getPartBrand() != null ? partModel.getPartBrand().getId() : null)
                .imageUrl(partModel.getImageUrl())
                .partCategoryId(partModel.getPartCategory() != null ? partModel.getPartCategory().getId() : null)
                .build();
    }

    public PartModel toEntity(PartModelDTO dto) {
        if (dto == null) {
            return null;
        }

        PartModel.PartModelBuilder builder = PartModel.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .imageUrl(dto.getImageUrl());

        if (dto.getPartBrandId() != null) {
            builder.partBrand(partBrandRepository.findById(dto.getPartBrandId()).orElse(null));
        }
        if (dto.getPartCategoryId() != null) {
            builder.partCategory(partCategoryRepository.findById(dto.getPartCategoryId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(PartModelDTO dto, PartModel partModel) {
        if (dto == null || partModel == null) {
            return;
        }

        if (dto.getName() != null) {
            partModel.setName(dto.getName());
        }
        if (dto.getImageUrl() != null) {
            partModel.setImageUrl(dto.getImageUrl());
        }
    }
}
