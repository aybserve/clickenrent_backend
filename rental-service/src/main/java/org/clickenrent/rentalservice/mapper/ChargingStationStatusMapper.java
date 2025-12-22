package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.ChargingStationStatusDTO;
import org.clickenrent.rentalservice.entity.ChargingStationStatus;
import org.springframework.stereotype.Component;

@Component
public class ChargingStationStatusMapper {

    public ChargingStationStatusDTO toDto(ChargingStationStatus status) {
        if (status == null) return null;
        return ChargingStationStatusDTO.builder().id(status.getId()).name(status.getName()).build();
    }

    public ChargingStationStatus toEntity(ChargingStationStatusDTO dto) {
        if (dto == null) return null;
        return ChargingStationStatus.builder().id(dto.getId()).name(dto.getName()).build();
    }

    public void updateEntityFromDto(ChargingStationStatusDTO dto, ChargingStationStatus status) {
        if (dto == null || status == null) return;
        if (dto.getName() != null) status.setName(dto.getName());
    }
}




