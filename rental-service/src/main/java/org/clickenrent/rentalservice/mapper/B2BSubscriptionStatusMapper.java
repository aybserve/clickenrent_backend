package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.B2BSubscriptionStatusDTO;
import org.clickenrent.rentalservice.entity.B2BSubscriptionStatus;
import org.springframework.stereotype.Component;

@Component
public class B2BSubscriptionStatusMapper {

    public B2BSubscriptionStatusDTO toDto(B2BSubscriptionStatus status) {
        if (status == null) return null;
        return B2BSubscriptionStatusDTO.builder().id(status.getId()).name(status.getName()).build();
    }

    public B2BSubscriptionStatus toEntity(B2BSubscriptionStatusDTO dto) {
        if (dto == null) return null;
        return B2BSubscriptionStatus.builder().id(dto.getId()).name(dto.getName()).build();
    }

    public void updateEntityFromDto(B2BSubscriptionStatusDTO dto, B2BSubscriptionStatus status) {
        if (dto == null || status == null) return;
        if (dto.getName() != null) status.setName(dto.getName());
    }
}




