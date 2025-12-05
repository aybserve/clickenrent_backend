package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.PartBrandDTO;
import org.clickenrent.rentalservice.entity.PartBrand;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between PartBrand entity and PartBrandDTO.
 */
@Component
public class PartBrandMapper {

    public PartBrandDTO toDto(PartBrand partBrand) {
        if (partBrand == null) {
            return null;
        }

        return PartBrandDTO.builder()
                .id(partBrand.getId())
                .name(partBrand.getName())
                .companyId(partBrand.getCompanyId())
                .build();
    }

    public PartBrand toEntity(PartBrandDTO dto) {
        if (dto == null) {
            return null;
        }

        return PartBrand.builder()
                .id(dto.getId())
                .name(dto.getName())
                .companyId(dto.getCompanyId())
                .build();
    }

    public void updateEntityFromDto(PartBrandDTO dto, PartBrand partBrand) {
        if (dto == null || partBrand == null) {
            return;
        }

        if (dto.getName() != null) {
            partBrand.setName(dto.getName());
        }
    }
}
