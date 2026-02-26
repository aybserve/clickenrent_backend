package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.B2BSubscriptionOrderStatusDTO;
import org.clickenrent.rentalservice.entity.B2BSubscriptionOrderStatus;
import org.springframework.stereotype.Component;

@Component
public class B2BSubscriptionOrderStatusMapper {

    public B2BSubscriptionOrderStatusDTO toDto(B2BSubscriptionOrderStatus status) {
        if (status == null) return null;
        return B2BSubscriptionOrderStatusDTO.builder()
                .id(status.getId())
                .externalId(status.getExternalId())
                .name(status.getName())
                .dateCreated(status.getDateCreated())
                .lastDateModified(status.getLastDateModified())
                .createdBy(status.getCreatedBy())
                .lastModifiedBy(status.getLastModifiedBy())
                .build();
    }

    public B2BSubscriptionOrderStatus toEntity(B2BSubscriptionOrderStatusDTO dto) {
        if (dto == null) return null;
        return B2BSubscriptionOrderStatus.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(B2BSubscriptionOrderStatusDTO dto, B2BSubscriptionOrderStatus status) {
        if (dto == null || status == null) return;
        if (dto.getExternalId() != null) status.setExternalId(dto.getExternalId());
        if (dto.getName() != null) status.setName(dto.getName());
    }
}








