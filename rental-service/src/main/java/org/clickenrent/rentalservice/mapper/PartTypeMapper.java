package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.PartTypeDTO;
import org.clickenrent.rentalservice.entity.PartType;
import org.springframework.stereotype.Component;

@Component
public class PartTypeMapper {

    public PartTypeDTO toDto(PartType type) {
        if (type == null) return null;
        return PartTypeDTO.builder().id(type.getId()).name(type.getName()).build();
    }

    public PartType toEntity(PartTypeDTO dto) {
        if (dto == null) return null;
        return PartType.builder().id(dto.getId()).name(dto.getName()).build();
    }

    public void updateEntityFromDto(PartTypeDTO dto, PartType type) {
        if (dto == null || type == null) return;
        if (dto.getName() != null) type.setName(dto.getName());
    }
}
