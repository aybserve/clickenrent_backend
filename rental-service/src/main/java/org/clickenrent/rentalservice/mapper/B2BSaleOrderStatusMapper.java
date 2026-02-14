package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.B2BSaleOrderStatusDTO;
import org.clickenrent.rentalservice.entity.B2BSaleOrderStatus;
import org.springframework.stereotype.Component;

@Component
public class B2BSaleOrderStatusMapper {

    public B2BSaleOrderStatusDTO toDto(B2BSaleOrderStatus status) {
        if (status == null) return null;
        return B2BSaleOrderStatusDTO.builder()
                .id(status.getId())
                .externalId(status.getExternalId())
                .name(status.getName())
                .dateCreated(status.getDateCreated())
                .lastDateModified(status.getLastDateModified())
                .createdBy(status.getCreatedBy())
                .lastModifiedBy(status.getLastModifiedBy())
                .build();
    }

    public B2BSaleOrderStatus toEntity(B2BSaleOrderStatusDTO dto) {
        if (dto == null) return null;
        return B2BSaleOrderStatus.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(B2BSaleOrderStatusDTO dto, B2BSaleOrderStatus status) {
        if (dto == null || status == null) return;
        if (dto.getExternalId() != null) status.setExternalId(dto.getExternalId());
        if (dto.getName() != null) status.setName(dto.getName());
    }
}








