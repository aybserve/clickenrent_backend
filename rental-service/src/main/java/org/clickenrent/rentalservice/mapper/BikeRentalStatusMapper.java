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
                .externalId(status.getExternalId())
                .name(status.getName())
                .dateCreated(status.getDateCreated())
                .lastDateModified(status.getLastDateModified())
                .createdBy(status.getCreatedBy())
                .lastModifiedBy(status.getLastModifiedBy())
                .build();
    }

    public BikeRentalStatus toEntity(BikeRentalStatusDTO dto) {
        if (dto == null) return null;
        return BikeRentalStatus.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(BikeRentalStatusDTO dto, BikeRentalStatus status) {
        if (dto == null || status == null) return;
        if (dto.getExternalId() != null) status.setExternalId(dto.getExternalId());
        if (dto.getName() != null) status.setName(dto.getName());
    }
}








