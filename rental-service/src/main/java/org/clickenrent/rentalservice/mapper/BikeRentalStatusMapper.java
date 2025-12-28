package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.BikeRentalStatusDTO;
import org.clickenrent.rentalservice.entity.BikeRentalStatus;
import org.springframework.stereotype.Component;

@Component
public class BikeRentalStatusMapper {

    public BikeRentalStatusDTO toDto(BikeRentalStatus status) {
        if (status == null) return null;
        return BikeRentalStatusDTO.builder()
                .id(status.getId())
                .name(status.getName())
                .build();
    }

    public BikeRentalStatus toEntity(BikeRentalStatusDTO dto) {
        if (dto == null) return null;
        return BikeRentalStatus.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(BikeRentalStatusDTO dto, BikeRentalStatus status) {
        if (dto == null || status == null) return;
        if (dto.getName() != null) status.setName(dto.getName());
    }
}








