package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LocationDTO;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.repository.CoordinatesRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Location entity and LocationDTO.
 */
@Component
@RequiredArgsConstructor
public class LocationMapper {

    private final CoordinatesRepository coordinatesRepository;

    public LocationDTO toDto(Location location) {
        if (location == null) {
            return null;
        }

        return LocationDTO.builder()
                .id(location.getId())
                .externalId(location.getExternalId())
                .erpPartnerId(location.getErpPartnerId())
                .name(location.getName())
                .address(location.getAddress())
                .description(location.getDescription())
                .companyId(location.getCompanyId())
                .isPublic(location.getIsPublic())
                .directions(location.getDirections())
                .coordinatesId(location.getCoordinates() != null ? location.getCoordinates().getId() : null)
                .dateCreated(location.getDateCreated())
                .lastDateModified(location.getLastDateModified())
                .createdBy(location.getCreatedBy())
                .lastModifiedBy(location.getLastModifiedBy())
                .build();
    }

    public Location toEntity(LocationDTO dto) {
        if (dto == null) {
            return null;
        }

        Location.LocationBuilder builder = Location.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .erpPartnerId(dto.getErpPartnerId())
                .name(dto.getName())
                .address(dto.getAddress())
                .description(dto.getDescription())
                .companyId(dto.getCompanyId())
                .isPublic(dto.getIsPublic())
                .directions(dto.getDirections());

        if (dto.getCoordinatesId() != null) {
            builder.coordinates(coordinatesRepository.findById(dto.getCoordinatesId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(LocationDTO dto, Location location) {
        if (dto == null || location == null) {
            return;
        }

        if (dto.getName() != null) {
            location.setName(dto.getName());
        }
        if (dto.getAddress() != null) {
            location.setAddress(dto.getAddress());
        }
        if (dto.getDescription() != null) {
            location.setDescription(dto.getDescription());
        }
        if (dto.getIsPublic() != null) {
            location.setIsPublic(dto.getIsPublic());
        }
        if (dto.getDirections() != null) {
            location.setDirections(dto.getDirections());
        }
    }
}


