package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.B2BSubscriptionStatusDTO;
import org.clickenrent.rentalservice.entity.B2BSubscriptionStatus;
import org.springframework.stereotype.Component;

@Component
public class B2BSubscriptionStatusMapper {

    public B2BSubscriptionStatusDTO toDto(B2BSubscriptionStatus status) {
        if (status == null) return null;
        return B2BSubscriptionStatusDTO.builder()
                .id(status.getId())
                .externalId(status.getExternalId())
                .name(status.getName())
                .dateCreated(status.getDateCreated())
                .lastDateModified(status.getLastDateModified())
                .createdBy(status.getCreatedBy())
                .lastModifiedBy(status.getLastModifiedBy())
                .build();
    }

    public B2BSubscriptionStatus toEntity(B2BSubscriptionStatusDTO dto) {
        if (dto == null) return null;
        return B2BSubscriptionStatus.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(B2BSubscriptionStatusDTO dto, B2BSubscriptionStatus status) {
        if (dto == null || status == null) return;
        if (dto.getExternalId() != null) status.setExternalId(dto.getExternalId());
        if (dto.getName() != null) status.setName(dto.getName());
    }
}








