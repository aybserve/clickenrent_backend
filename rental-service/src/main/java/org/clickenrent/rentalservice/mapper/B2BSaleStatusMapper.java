package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.B2BSaleStatusDTO;
import org.clickenrent.rentalservice.entity.B2BSaleStatus;
import org.springframework.stereotype.Component;

@Component
public class B2BSaleStatusMapper {

    public B2BSaleStatusDTO toDto(B2BSaleStatus status) {
        if (status == null) return null;
        return B2BSaleStatusDTO.builder().id(status.getId()).name(status.getName()).build();
    }

    public B2BSaleStatus toEntity(B2BSaleStatusDTO dto) {
        if (dto == null) return null;
        return B2BSaleStatus.builder().id(dto.getId()).name(dto.getName()).build();
    }

    public void updateEntityFromDto(B2BSaleStatusDTO dto, B2BSaleStatus status) {
        if (dto == null || status == null) return;
        if (dto.getName() != null) status.setName(dto.getName());
    }
}






