package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LocationImageDTO;
import org.clickenrent.rentalservice.entity.LocationImage;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between LocationImage entity and LocationImageDTO.
 */
@Component
@RequiredArgsConstructor
public class LocationImageMapper {

    private final LocationRepository locationRepository;

    public LocationImageDTO toDto(LocationImage locationImage) {
        if (locationImage == null) {
            return null;
        }

        return LocationImageDTO.builder()
                .id(locationImage.getId())
                .externalId(locationImage.getExternalId())
                .locationId(locationImage.getLocation() != null ? locationImage.getLocation().getId() : null)
                .imageUrl(locationImage.getImageUrl())
                .sortOrder(locationImage.getSortOrder())
                .isThumbnail(locationImage.getIsThumbnail())
                .dateCreated(locationImage.getDateCreated())
                .lastDateModified(locationImage.getLastDateModified())
                .createdBy(locationImage.getCreatedBy())
                .lastModifiedBy(locationImage.getLastModifiedBy())
                .build();
    }

    public LocationImage toEntity(LocationImageDTO dto) {
        if (dto == null) {
            return null;
        }

        LocationImage.LocationImageBuilder builder = LocationImage.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .imageUrl(dto.getImageUrl())
                .sortOrder(dto.getSortOrder())
                .isThumbnail(dto.getIsThumbnail());

        if (dto.getLocationId() != null) {
            builder.location(locationRepository.findById(dto.getLocationId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(LocationImageDTO dto, LocationImage locationImage) {
        if (dto == null || locationImage == null) {
            return;
        }

        if (dto.getImageUrl() != null) {
            locationImage.setImageUrl(dto.getImageUrl());
        }
        if (dto.getSortOrder() != null) {
            locationImage.setSortOrder(dto.getSortOrder());
        }
        if (dto.getIsThumbnail() != null) {
            locationImage.setIsThumbnail(dto.getIsThumbnail());
        }
    }
}
