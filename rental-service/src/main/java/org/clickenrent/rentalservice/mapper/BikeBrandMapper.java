package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.BikeBrandDTO;
import org.clickenrent.rentalservice.entity.BikeBrand;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeBrand entity and BikeBrandDTO.
 */
@Component
public class BikeBrandMapper {

    public BikeBrandDTO toDto(BikeBrand bikeBrand) {
        if (bikeBrand == null) {
            return null;
        }

        return BikeBrandDTO.builder()
                .id(bikeBrand.getId())
                .externalId(bikeBrand.getExternalId())
                .name(bikeBrand.getName())
                .companyExternalId(bikeBrand.getCompanyExternalId())
                .dateCreated(bikeBrand.getDateCreated())
                .lastDateModified(bikeBrand.getLastDateModified())
                .createdBy(bikeBrand.getCreatedBy())
                .lastModifiedBy(bikeBrand.getLastModifiedBy())
                .build();
    }

    public BikeBrand toEntity(BikeBrandDTO dto) {
        if (dto == null) {
            return null;
        }

        return BikeBrand.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .companyExternalId(dto.getCompanyExternalId())
                .build();
    }

    public void updateEntityFromDto(BikeBrandDTO dto, BikeBrand bikeBrand) {
        if (dto == null || bikeBrand == null) {
            return;
        }

        if (dto.getName() != null) {
            bikeBrand.setName(dto.getName());
        }
    }
}




