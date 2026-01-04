package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.B2BSaleStatusDTO;
import org.clickenrent.rentalservice.entity.B2BSaleStatus;
import org.springframework.stereotype.Component;

@Component
public class B2BSaleStatusMapper {

    public B2BSaleStatusDTO toDto(B2BSaleStatus status) {
        if (status == null) return null;
        return B2BSaleStatusDTO.builder()
                .id(status.getId())
                .externalId(status.getExternalId())
                .name(status.getName())
                .dateCreated(status.getDateCreated())
                .lastDateModified(status.getLastDateModified())
                .createdBy(status.getCreatedBy())
                .lastModifiedBy(status.getLastModifiedBy())
                .build();
    }

    public B2BSaleStatus toEntity(B2BSaleStatusDTO dto) {
        if (dto == null) return null;
        return B2BSaleStatus.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(B2BSaleStatusDTO dto, B2BSaleStatus status) {
        if (dto == null || status == null) return;
        if (dto.getExternalId() != null) status.setExternalId(dto.getExternalId());
        if (dto.getName() != null) status.setName(dto.getName());
    }
}








