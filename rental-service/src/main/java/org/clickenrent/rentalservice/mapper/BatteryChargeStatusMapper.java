package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.BatteryChargeStatusDTO;
import org.clickenrent.rentalservice.entity.BatteryChargeStatus;
import org.springframework.stereotype.Component;

@Component
public class BatteryChargeStatusMapper {

    public BatteryChargeStatusDTO toDto(BatteryChargeStatus status) {
        if (status == null) return null;
        return BatteryChargeStatusDTO.builder().id(status.getId()).name(status.getName()).build();
    }

    public BatteryChargeStatus toEntity(BatteryChargeStatusDTO dto) {
        if (dto == null) return null;
        return BatteryChargeStatus.builder().id(dto.getId()).name(dto.getName()).build();
    }

    public void updateEntityFromDto(BatteryChargeStatusDTO dto, BatteryChargeStatus status) {
        if (dto == null || status == null) return;
        if (dto.getName() != null) status.setName(dto.getName());
    }
}
