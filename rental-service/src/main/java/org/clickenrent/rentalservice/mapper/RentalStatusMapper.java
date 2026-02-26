package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.RentalStatusDTO;
import org.clickenrent.rentalservice.entity.RentalStatus;
import org.springframework.stereotype.Component;

@Component
public class RentalStatusMapper {

    public RentalStatusDTO toDto(RentalStatus status) {
        if (status == null) return null;
        return RentalStatusDTO.builder()
                .id(status.getId())
                .externalId(status.getExternalId())
                .name(status.getName())
                .dateCreated(status.getDateCreated())
                .lastDateModified(status.getLastDateModified())
                .createdBy(status.getCreatedBy())
                .lastModifiedBy(status.getLastModifiedBy())
                .build();
    }

    public RentalStatus toEntity(RentalStatusDTO dto) {
        if (dto == null) return null;
        return RentalStatus.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(RentalStatusDTO dto, RentalStatus status) {
        if (dto == null || status == null) return;
        if (dto.getExternalId() != null) status.setExternalId(dto.getExternalId());
        if (dto.getName() != null) status.setName(dto.getName());
    }
}








