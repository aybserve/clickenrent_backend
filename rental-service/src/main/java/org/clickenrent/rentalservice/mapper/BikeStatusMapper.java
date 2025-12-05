package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.BikeStatusDTO;
import org.clickenrent.rentalservice.entity.BikeStatus;
import org.springframework.stereotype.Component;

@Component
public class BikeStatusMapper {

    public BikeStatusDTO toDto(BikeStatus bikeStatus) {
        if (bikeStatus == null) return null;
        return BikeStatusDTO.builder().id(bikeStatus.getId()).name(bikeStatus.getName()).build();
    }

    public BikeStatus toEntity(BikeStatusDTO dto) {
        if (dto == null) return null;
        return BikeStatus.builder().id(dto.getId()).name(dto.getName()).build();
    }

    public void updateEntityFromDto(BikeStatusDTO dto, BikeStatus bikeStatus) {
        if (dto == null || bikeStatus == null) return;
        if (dto.getName() != null) bikeStatus.setName(dto.getName());
    }
}
