package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.rentalservice.dto.UserLocationDTO;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.entity.UserLocation;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.UserLocationMapper;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.clickenrent.rentalservice.repository.UserLocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing UserLocation entities.
 * Assigns users to locations with specific roles.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserLocationService {

    private final UserLocationRepository userLocationRepository;
    private final LocationRepository locationRepository;
    private final UserLocationMapper userLocationMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<UserLocationDTO> getUserLocationsByUserExternalId(String userExternalId) {
        if (!securityService.isAdmin() && !securityService.hasAccessToUserByExternalId(userExternalId)) {
            throw new UnauthorizedException("You don't have permission to view user locations");
        }

        return userLocationRepository.findByUserExternalId(userExternalId).stream()
                .map(userLocationMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserLocationDTO> getUserLocationsByLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", locationId));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(location.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to view location users");
        }

        return userLocationRepository.findByLocation(location).stream()
                .map(userLocationMapper::toDto)
                .toList();
    }

    @Transactional
    public UserLocationDTO assignUserToLocation(UserLocationDTO dto) {
        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", dto.getLocationId()));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(location.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to assign users to this location");
        }

        UserLocation userLocation = userLocationMapper.toEntity(dto);
        userLocation = userLocationRepository.save(userLocation);
        return userLocationMapper.toDto(userLocation);
    }

    @Transactional
    public void removeUserFromLocation(Long id) {
        UserLocation userLocation = userLocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserLocation", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(userLocation.getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to remove users from this location");
        }

        userLocationRepository.delete(userLocation);
    }

    @Transactional(readOnly = true)
    public UserLocationDTO getUserLocationByExternalId(String externalId) {
        UserLocation userLocation = userLocationRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("UserLocation", "externalId", externalId));
        return userLocationMapper.toDto(userLocation);
    }
}




