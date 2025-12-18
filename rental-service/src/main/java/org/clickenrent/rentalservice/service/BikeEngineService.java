package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeEngineDTO;
import org.clickenrent.rentalservice.entity.BikeEngine;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeEngineMapper;
import org.clickenrent.rentalservice.repository.BikeEngineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BikeEngineService {

    private final BikeEngineRepository bikeEngineRepository;
    private final BikeEngineMapper bikeEngineMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<BikeEngineDTO> getAllBikeEngines(Pageable pageable) {
        return bikeEngineRepository.findAll(pageable)
                .map(bikeEngineMapper::toDto);
    }

    @Transactional(readOnly = true)
    public BikeEngineDTO getBikeEngineById(Long id) {
        BikeEngine bikeEngine = bikeEngineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeEngine", "id", id));
        return bikeEngineMapper.toDto(bikeEngine);
    }

    @Transactional
    public BikeEngineDTO createBikeEngine(BikeEngineDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bike engines");
        }

        BikeEngine bikeEngine = bikeEngineMapper.toEntity(dto);
        bikeEngine = bikeEngineRepository.save(bikeEngine);
        return bikeEngineMapper.toDto(bikeEngine);
    }

    @Transactional
    public BikeEngineDTO updateBikeEngine(Long id, BikeEngineDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike engines");
        }

        BikeEngine bikeEngine = bikeEngineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeEngine", "id", id));

        bikeEngineMapper.updateEntityFromDto(dto, bikeEngine);
        bikeEngine = bikeEngineRepository.save(bikeEngine);
        return bikeEngineMapper.toDto(bikeEngine);
    }

    @Transactional
    public void deleteBikeEngine(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike engines");
        }

        BikeEngine bikeEngine = bikeEngineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeEngine", "id", id));
        bikeEngineRepository.delete(bikeEngine);
    }
}

