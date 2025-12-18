package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.ChargingStationBrandDTO;
import org.clickenrent.rentalservice.entity.ChargingStationBrand;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between ChargingStationBrand entity and ChargingStationBrandDTO.
 */
@Component
public class ChargingStationBrandMapper {

    public ChargingStationBrandDTO toDto(ChargingStationBrand brand) {
        if (brand == null) {
            return null;
        }

        return ChargingStationBrandDTO.builder()
                .id(brand.getId())
                .name(brand.getName())
                .companyId(brand.getCompanyId())
                .build();
    }

    public ChargingStationBrand toEntity(ChargingStationBrandDTO dto) {
        if (dto == null) {
            return null;
        }

        return ChargingStationBrand.builder()
                .id(dto.getId())
                .name(dto.getName())
                .companyId(dto.getCompanyId())
                .build();
    }

    public void updateEntityFromDto(ChargingStationBrandDTO dto, ChargingStationBrand brand) {
        if (dto == null || brand == null) {
            return;
        }

        if (dto.getName() != null) {
            brand.setName(dto.getName());
        }
    }
}


