package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.RideStatusDTO;
import org.clickenrent.rentalservice.entity.RideStatus;
import org.springframework.stereotype.Component;

@Component
public class RideStatusMapper {

    public RideStatusDTO toDto(RideStatus status) {
        if (status == null) return null;
        return RideStatusDTO.builder()
                .id(status.getId())
                .externalId(status.getExternalId())
                .name(status.getName())
                .dateCreated(status.getDateCreated())
                .lastDateModified(status.getLastDateModified())
                .createdBy(status.getCreatedBy())
                .lastModifiedBy(status.getLastModifiedBy())
                .build();
    }

    public RideStatus toEntity(RideStatusDTO dto) {
        if (dto == null) return null;
        return RideStatus.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(RideStatusDTO dto, RideStatus status) {
        if (dto == null || status == null) return;
        if (dto.getExternalId() != null) status.setExternalId(dto.getExternalId());
        if (dto.getName() != null) status.setName(dto.getName());
    }
}








