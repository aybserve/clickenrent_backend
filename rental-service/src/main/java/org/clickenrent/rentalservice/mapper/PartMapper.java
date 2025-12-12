package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.PartDTO;
import org.clickenrent.rentalservice.entity.Part;
import org.clickenrent.rentalservice.repository.HubRepository;
import org.clickenrent.rentalservice.repository.PartBrandRepository;
import org.clickenrent.rentalservice.repository.PartCategoryRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Part entity and PartDTO.
 */
@Component
@RequiredArgsConstructor
public class PartMapper {

    private final PartBrandRepository partBrandRepository;
    private final PartCategoryRepository partCategoryRepository;
    private final HubRepository hubRepository;

    public PartDTO toDto(Part part) {
        if (part == null) {
            return null;
        }

        return PartDTO.builder()
                .id(part.getId())
                .externalId(part.getExternalId())
                .name(part.getName())
                .partBrandId(part.getPartBrand() != null ? part.getPartBrand().getId() : null)
                .imageUrl(part.getImageUrl())
                .partCategoryId(part.getPartCategory() != null ? part.getPartCategory().getId() : null)
                .hubId(part.getHub() != null ? part.getHub().getId() : null)
                .vat(part.getVat())
                .isVatInclude(part.getIsVatInclude())
                .isB2BRentable(part.getIsB2BRentable())
                .b2bSalePrice(part.getB2bSalePrice())
                .quantity(part.getQuantity())
                .dateCreated(part.getDateCreated())
                .lastDateModified(part.getLastDateModified())
                .createdBy(part.getCreatedBy())
                .lastModifiedBy(part.getLastModifiedBy())
                .build();
    }

    public Part toEntity(PartDTO dto) {
        if (dto == null) {
            return null;
        }

        Part.PartBuilder<?, ?> builder = Part.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .imageUrl(dto.getImageUrl())
                .vat(dto.getVat())
                .isVatInclude(dto.getIsVatInclude())
                .isB2BRentable(dto.getIsB2BRentable())
                .b2bSalePrice(dto.getB2bSalePrice())
                .quantity(dto.getQuantity());

        if (dto.getPartBrandId() != null) {
            builder.partBrand(partBrandRepository.findById(dto.getPartBrandId()).orElse(null));
        }
        if (dto.getPartCategoryId() != null) {
            builder.partCategory(partCategoryRepository.findById(dto.getPartCategoryId()).orElse(null));
        }
        if (dto.getHubId() != null) {
            builder.hub(hubRepository.findById(dto.getHubId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(PartDTO dto, Part part) {
        if (dto == null || part == null) {
            return;
        }

        if (dto.getName() != null) {
            part.setName(dto.getName());
        }
        if (dto.getImageUrl() != null) {
            part.setImageUrl(dto.getImageUrl());
        }
        if (dto.getVat() != null) {
            part.setVat(dto.getVat());
        }
        if (dto.getIsVatInclude() != null) {
            part.setIsVatInclude(dto.getIsVatInclude());
        }
        if (dto.getPartBrandId() != null) {
            partBrandRepository.findById(dto.getPartBrandId()).ifPresent(part::setPartBrand);
        }
        if (dto.getPartCategoryId() != null) {
            partCategoryRepository.findById(dto.getPartCategoryId()).ifPresent(part::setPartCategory);
        }
        if (dto.getHubId() != null) {
            hubRepository.findById(dto.getHubId()).ifPresent(part::setHub);
        }
        if (dto.getB2bSalePrice() != null) {
            part.setB2bSalePrice(dto.getB2bSalePrice());
        }
        if (dto.getQuantity() != null) {
            part.setQuantity(dto.getQuantity());
        }
    }
}
