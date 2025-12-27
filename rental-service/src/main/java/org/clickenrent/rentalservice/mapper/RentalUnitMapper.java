package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.RentalUnitDTO;
import org.clickenrent.rentalservice.entity.RentalUnit;
import org.springframework.stereotype.Component;

@Component
public class RentalUnitMapper {

    public RentalUnitDTO toDto(RentalUnit unit) {
        if (unit == null) return null;
        return RentalUnitDTO.builder().id(unit.getId()).name(unit.getName()).build();
    }

    public RentalUnit toEntity(RentalUnitDTO dto) {
        if (dto == null) return null;
        return RentalUnit.builder().id(dto.getId()).name(dto.getName()).build();
    }

    public void updateEntityFromDto(RentalUnitDTO dto, RentalUnit unit) {
        if (dto == null || unit == null) return;
        if (dto.getName() != null) unit.setName(dto.getName());
    }
}







