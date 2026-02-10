package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionStatusDTO;
import org.clickenrent.supportservice.entity.BikeInspectionStatus;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeInspectionStatusMapper;
import org.clickenrent.supportservice.repository.BikeInspectionStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing BikeInspectionStatus entities.
 */
@Service
@RequiredArgsConstructor
public class BikeInspectionStatusService {

    private final BikeInspectionStatusRepository bikeInspectionStatusRepository;
    private final BikeInspectionStatusMapper bikeInspectionStatusMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeInspectionStatusDTO> getAll() {
        return bikeInspectionStatusRepository.findAll().stream()
                .map(bikeInspectionStatusMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BikeInspectionStatusDTO getById(Long id) {
        BikeInspectionStatus entity = bikeInspectionStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionStatus", "id", id));
        return bikeInspectionStatusMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeInspectionStatusDTO getByExternalId(String externalId) {
        BikeInspectionStatus entity = bikeInspectionStatusRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionStatus", "externalId", externalId));
        return bikeInspectionStatusMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeInspectionStatusDTO getByName(String name) {
        BikeInspectionStatus entity = bikeInspectionStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionStatus", "name", name));
        return bikeInspectionStatusMapper.toDto(entity);
    }

    @Transactional
    public BikeInspectionStatusDTO create(BikeInspectionStatusDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bike inspection statuses");
        }

        BikeInspectionStatus entity = bikeInspectionStatusMapper.toEntity(dto);
        entity.sanitizeForCreate();
        entity = bikeInspectionStatusRepository.save(entity);
        return bikeInspectionStatusMapper.toDto(entity);
    }

    @Transactional
    public BikeInspectionStatusDTO update(Long id, BikeInspectionStatusDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike inspection statuses");
        }

        BikeInspectionStatus entity = bikeInspectionStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionStatus", "id", id));

        bikeInspectionStatusMapper.updateEntityFromDto(dto, entity);
        entity = bikeInspectionStatusRepository.save(entity);
        return bikeInspectionStatusMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike inspection statuses");
        }

        BikeInspectionStatus entity = bikeInspectionStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionStatus", "id", id));
        bikeInspectionStatusRepository.delete(entity);
    }
}
