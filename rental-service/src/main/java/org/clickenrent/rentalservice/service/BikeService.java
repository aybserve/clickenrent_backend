package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeDTO;
import org.clickenrent.rentalservice.entity.Bike;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeMapper;
import org.clickenrent.rentalservice.repository.BikeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing Bike entities.
 */
@Service
@RequiredArgsConstructor
public class BikeService {

    private final BikeRepository bikeRepository;
    private final BikeMapper bikeMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<BikeDTO> getAllBikes(Pageable pageable) {
        // Admin can see all bikes
        if (securityService.isAdmin()) {
            return bikeRepository.findAll(pageable)
                    .map(bikeMapper::toDto);
        }

        // B2B/Customer see bikes based on their company access
        throw new UnauthorizedException("You don't have permission to view all bikes");
    }

    @Transactional(readOnly = true)
    public BikeDTO getBikeById(Long id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "id", id));
        return bikeMapper.toDto(bike);
    }

    @Transactional(readOnly = true)
    public BikeDTO getBikeByCode(String code) {
        Bike bike = bikeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "code", code));
        return bikeMapper.toDto(bike);
    }

    @Transactional
    public BikeDTO createBike(BikeDTO bikeDTO) {
        // Only admins can create bikes
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bikes");
        }

        Bike bike = bikeMapper.toEntity(bikeDTO);
        bike = bikeRepository.save(bike);
        return bikeMapper.toDto(bike);
    }

    @Transactional
    public BikeDTO updateBike(Long id, BikeDTO bikeDTO) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "id", id));

        // Only admins can update bikes
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bikes");
        }

        bikeMapper.updateEntityFromDto(bikeDTO, bike);
        bike = bikeRepository.save(bike);
        return bikeMapper.toDto(bike);
    }

    @Transactional
    public void deleteBike(Long id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "id", id));

        // Only admins can delete bikes
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bikes");
        }

        bikeRepository.delete(bike);
    }
}

