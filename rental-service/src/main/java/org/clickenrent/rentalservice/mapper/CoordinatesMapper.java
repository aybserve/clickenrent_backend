package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.CoordinatesDTO;
import org.clickenrent.rentalservice.entity.Coordinates;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Coordinates entity and CoordinatesDTO.
 */
@Component
public class CoordinatesMapper {

    public CoordinatesDTO toDto(Coordinates coordinates) {
        if (coordinates == null) {
            return null;
        }

        return CoordinatesDTO.builder()
                .id(coordinates.getId())
                .externalId(coordinates.getExternalId())
                .latitude(coordinates.getLatitude())
                .longitude(coordinates.getLongitude())
                .dateCreated(coordinates.getDateCreated())
                .lastDateModified(coordinates.getLastDateModified())
                .createdBy(coordinates.getCreatedBy())
                .lastModifiedBy(coordinates.getLastModifiedBy())
                .build();
    }

    public Coordinates toEntity(CoordinatesDTO dto) {
        if (dto == null) {
            return null;
        }

        return Coordinates.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
    }

    public void updateEntityFromDto(CoordinatesDTO dto, Coordinates coordinates) {
        if (dto == null || coordinates == null) {
            return;
        }

        if (dto.getExternalId() != null) {
            coordinates.setExternalId(dto.getExternalId());
        }
        if (dto.getLatitude() != null) {
            coordinates.setLatitude(dto.getLatitude());
        }
        if (dto.getLongitude() != null) {
            coordinates.setLongitude(dto.getLongitude());
        }
    }
}








