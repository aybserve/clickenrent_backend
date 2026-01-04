package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.CoordinatesDTO;
import org.clickenrent.rentalservice.entity.Coordinates;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.CoordinatesMapper;
import org.clickenrent.rentalservice.repository.CoordinatesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing Coordinates entities.
 */
@Service
@RequiredArgsConstructor
public class CoordinatesService {

    private final CoordinatesRepository coordinatesRepository;
    private final CoordinatesMapper coordinatesMapper;

    @Transactional(readOnly = true)
    public CoordinatesDTO getCoordinatesById(Long id) {
        Coordinates coordinates = coordinatesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinates", "id", id));
        return coordinatesMapper.toDto(coordinates);
    }

    @Transactional
    public CoordinatesDTO createCoordinates(CoordinatesDTO dto) {
        Coordinates coordinates = coordinatesMapper.toEntity(dto);
        coordinates = coordinatesRepository.save(coordinates);
        return coordinatesMapper.toDto(coordinates);
    }

    @Transactional
    public CoordinatesDTO updateCoordinates(Long id, CoordinatesDTO dto) {
        Coordinates coordinates = coordinatesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinates", "id", id));

        coordinatesMapper.updateEntityFromDto(dto, coordinates);
        coordinates = coordinatesRepository.save(coordinates);
        return coordinatesMapper.toDto(coordinates);
    }

    @Transactional
    public void deleteCoordinates(Long id) {
        Coordinates coordinates = coordinatesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinates", "id", id));
        coordinatesRepository.delete(coordinates);
    }

    @Transactional
    public Coordinates createOrUpdateCoordinates(Coordinates existing, CoordinatesDTO dto) {
        if (existing != null) {
            // Update existing coordinates
            existing.setLatitude(dto.getLatitude());
            existing.setLongitude(dto.getLongitude());
            return coordinatesRepository.save(existing);
        } else {
            // Create new coordinates
            Coordinates newCoordinates = coordinatesMapper.toEntity(dto);
            return coordinatesRepository.save(newCoordinates);
        }
    }

    @Transactional(readOnly = true)
    public CoordinatesDTO findByExternalId(String externalId) {
        Coordinates coordinates = coordinatesRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinates", "externalId", externalId));
        return coordinatesMapper.toDto(coordinates);
    }
}
