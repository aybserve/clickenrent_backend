package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.BikeTypeDTO;
import org.clickenrent.rentalservice.entity.BikeType;
import org.springframework.stereotype.Component;

@Component
public class BikeTypeMapper {

    public BikeTypeDTO toDto(BikeType bikeType) {
        if (bikeType == null) return null;
        return BikeTypeDTO.builder().id(bikeType.getId()).name(bikeType.getName()).build();
    }

    public BikeType toEntity(BikeTypeDTO dto) {
        if (dto == null) return null;
        return BikeType.builder().id(dto.getId()).name(dto.getName()).build();
    }

    public void updateEntityFromDto(BikeTypeDTO dto, BikeType bikeType) {
        if (dto == null || bikeType == null) return;
        if (dto.getName() != null) bikeType.setName(dto.getName());
    }
}
