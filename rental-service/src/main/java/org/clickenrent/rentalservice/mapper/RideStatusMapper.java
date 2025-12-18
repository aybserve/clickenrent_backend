package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.RideStatusDTO;
import org.clickenrent.rentalservice.entity.RideStatus;
import org.springframework.stereotype.Component;

@Component
public class RideStatusMapper {

    public RideStatusDTO toDto(RideStatus status) {
        if (status == null) return null;
        return RideStatusDTO.builder().id(status.getId()).name(status.getName()).build();
    }

    public RideStatus toEntity(RideStatusDTO dto) {
        if (dto == null) return null;
        return RideStatus.builder().id(dto.getId()).name(dto.getName()).build();
    }

    public void updateEntityFromDto(RideStatusDTO dto, RideStatus status) {
        if (dto == null || status == null) return;
        if (dto.getName() != null) status.setName(dto.getName());
    }
}


