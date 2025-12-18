package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LocationImageDTO;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.entity.LocationImage;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.LocationImageMapper;
import org.clickenrent.rentalservice.repository.LocationImageRepository;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing LocationImage entities.
 */
@Service
@RequiredArgsConstructor
public class LocationImageService {

    private final LocationImageRepository locationImageRepository;
    private final LocationRepository locationRepository;
    private final LocationImageMapper locationImageMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<LocationImageDTO> getImagesByLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", locationId));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(location.getCompanyId())) {
            throw new UnauthorizedException("You don't have permission to view images for this location");
        }

        return locationImageRepository.findByLocation(location).stream()
                .map(locationImageMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public LocationImageDTO getImageById(Long id) {
        LocationImage locationImage = locationImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationImage", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(locationImage.getLocation().getCompanyId())) {
            throw new UnauthorizedException("You don't have permission to view this image");
        }

        return locationImageMapper.toDto(locationImage);
    }

    @Transactional
    public LocationImageDTO createImage(LocationImageDTO dto) {
        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", dto.getLocationId()));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(location.getCompanyId())) {
            throw new UnauthorizedException("You don't have permission to create images for this location");
        }

        LocationImage locationImage = locationImageMapper.toEntity(dto);
        locationImage = locationImageRepository.save(locationImage);
        return locationImageMapper.toDto(locationImage);
    }

    @Transactional
    public LocationImageDTO updateImage(Long id, LocationImageDTO dto) {
        LocationImage locationImage = locationImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationImage", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(locationImage.getLocation().getCompanyId())) {
            throw new UnauthorizedException("You don't have permission to update this image");
        }

        locationImageMapper.updateEntityFromDto(dto, locationImage);
        locationImage = locationImageRepository.save(locationImage);
        return locationImageMapper.toDto(locationImage);
    }

    @Transactional
    public void deleteImage(Long id) {
        LocationImage locationImage = locationImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationImage", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(locationImage.getLocation().getCompanyId())) {
            throw new UnauthorizedException("You don't have permission to delete this image");
        }

        locationImageRepository.delete(locationImage);
    }
}

