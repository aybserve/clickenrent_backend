package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.UserLocationDTO;
import org.clickenrent.rentalservice.entity.UserLocation;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.clickenrent.rentalservice.repository.LocationRoleRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between UserLocation entity and UserLocationDTO.
 */
@Component
@RequiredArgsConstructor
public class UserLocationMapper {

    private final LocationRepository locationRepository;
    private final LocationRoleRepository locationRoleRepository;

    public UserLocationDTO toDto(UserLocation userLocation) {
        if (userLocation == null) {
            return null;
        }

        return UserLocationDTO.builder()
                .id(userLocation.getId())
                .userId(userLocation.getUserId())
                .locationId(userLocation.getLocation() != null ? userLocation.getLocation().getId() : null)
                .locationRoleId(userLocation.getLocationRole() != null ? userLocation.getLocationRole().getId() : null)
                .build();
    }

    public UserLocation toEntity(UserLocationDTO dto) {
        if (dto == null) {
            return null;
        }

        UserLocation.UserLocationBuilder builder = UserLocation.builder()
                .id(dto.getId())
                .userId(dto.getUserId());

        if (dto.getLocationId() != null) {
            builder.location(locationRepository.findById(dto.getLocationId()).orElse(null));
        }
        if (dto.getLocationRoleId() != null) {
            builder.locationRole(locationRoleRepository.findById(dto.getLocationRoleId()).orElse(null));
        }

        return builder.build();
    }
}


