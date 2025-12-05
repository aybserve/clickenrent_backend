package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
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
@Service
@RequiredArgsConstructor
public class UserLocationService {

    private final UserLocationRepository userLocationRepository;
    private final LocationRepository locationRepository;
    private final UserLocationMapper userLocationMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<UserLocationDTO> getUserLocationsByUser(Long userId) {
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(userId)) {
            throw new UnauthorizedException("You don't have permission to view user locations");
        }

        return userLocationRepository.findByUserId(userId).stream()
                .map(userLocationMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserLocationDTO> getUserLocationsByLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", locationId));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(location.getCompanyId())) {
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

        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(location.getCompanyId())) {
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

        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(userLocation.getLocation().getCompanyId())) {
            throw new UnauthorizedException("You don't have permission to remove users from this location");
        }

        userLocationRepository.delete(userLocation);
    }
}
