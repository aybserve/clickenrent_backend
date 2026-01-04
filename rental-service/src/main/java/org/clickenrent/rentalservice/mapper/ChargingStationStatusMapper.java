package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.ChargingStationStatusDTO;
import org.clickenrent.rentalservice.entity.ChargingStationStatus;
import org.springframework.stereotype.Component;

@Component
public class ChargingStationStatusMapper {

    public ChargingStationStatusDTO toDto(ChargingStationStatus status) {
        if (status == null) return null;
        return ChargingStationStatusDTO.builder()
                .id(status.getId())
                .externalId(status.getExternalId())
                .name(status.getName())
                .dateCreated(status.getDateCreated())
                .lastDateModified(status.getLastDateModified())
                .createdBy(status.getCreatedBy())
                .lastModifiedBy(status.getLastModifiedBy())
                .build();
    }

    public ChargingStationStatus toEntity(ChargingStationStatusDTO dto) {
        if (dto == null) return null;
        return ChargingStationStatus.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(ChargingStationStatusDTO dto, ChargingStationStatus status) {
        if (dto == null || status == null) return;
        if (dto.getExternalId() != null) status.setExternalId(dto.getExternalId());
        if (dto.getName() != null) status.setName(dto.getName());
    }
}








