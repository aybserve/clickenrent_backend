package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.BikeEngineDTO;
import org.clickenrent.rentalservice.entity.BikeEngine;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeEngine entity and BikeEngineDTO.
 */
@Component
public class BikeEngineMapper {

    public BikeEngineDTO toDto(BikeEngine bikeEngine) {
        if (bikeEngine == null) {
            return null;
        }

        return BikeEngineDTO.builder()
                .id(bikeEngine.getId())
                .externalId(bikeEngine.getExternalId())
                .name(bikeEngine.getName())
                .build();
    }

    public BikeEngine toEntity(BikeEngineDTO dto) {
        if (dto == null) {
            return null;
        }

        return BikeEngine.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .build();
    }

    public void updateEntityFromDto(BikeEngineDTO dto, BikeEngine bikeEngine) {
        if (dto == null || bikeEngine == null) {
            return;
        }

        if (dto.getName() != null) {
            bikeEngine.setName(dto.getName());
        }
    }
}






