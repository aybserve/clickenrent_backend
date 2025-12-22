package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.HubDTO;
import org.clickenrent.rentalservice.entity.Hub;
import org.clickenrent.rentalservice.repository.CoordinatesRepository;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Hub entity and HubDTO.
 */
@Component
@RequiredArgsConstructor
public class HubMapper {

    private final LocationRepository locationRepository;
    private final CoordinatesRepository coordinatesRepository;

    public HubDTO toDto(Hub hub) {
        if (hub == null) {
            return null;
        }

        return HubDTO.builder()
                .id(hub.getId())
                .externalId(hub.getExternalId())
                .name(hub.getName())
                .locationId(hub.getLocation() != null ? hub.getLocation().getId() : null)
                .directions(hub.getDirections())
                .coordinatesId(hub.getCoordinates() != null ? hub.getCoordinates().getId() : null)
                .build();
    }

    public Hub toEntity(HubDTO dto) {
        if (dto == null) {
            return null;
        }

        Hub.HubBuilder builder = Hub.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .directions(dto.getDirections());

        if (dto.getLocationId() != null) {
            builder.location(locationRepository.findById(dto.getLocationId()).orElse(null));
        }
        if (dto.getCoordinatesId() != null) {
            builder.coordinates(coordinatesRepository.findById(dto.getCoordinatesId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(HubDTO dto, Hub hub) {
        if (dto == null || hub == null) {
            return;
        }

        if (dto.getName() != null) {
            hub.setName(dto.getName());
        }
        if (dto.getDirections() != null) {
            hub.setDirections(dto.getDirections());
        }
    }
}




