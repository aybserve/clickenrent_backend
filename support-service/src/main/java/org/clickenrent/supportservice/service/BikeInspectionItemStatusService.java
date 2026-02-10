package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemStatusDTO;
import org.clickenrent.supportservice.entity.BikeInspectionItemStatus;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeInspectionItemStatusMapper;
import org.clickenrent.supportservice.repository.BikeInspectionItemStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing BikeInspectionItemStatus entities.
 */
@Service
@RequiredArgsConstructor
public class BikeInspectionItemStatusService {

    private final BikeInspectionItemStatusRepository bikeInspectionItemStatusRepository;
    private final BikeInspectionItemStatusMapper bikeInspectionItemStatusMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeInspectionItemStatusDTO> getAll() {
        return bikeInspectionItemStatusRepository.findAll().stream()
                .map(bikeInspectionItemStatusMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BikeInspectionItemStatusDTO getById(Long id) {
        BikeInspectionItemStatus entity = bikeInspectionItemStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemStatus", "id", id));
        return bikeInspectionItemStatusMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeInspectionItemStatusDTO getByExternalId(String externalId) {
        BikeInspectionItemStatus entity = bikeInspectionItemStatusRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemStatus", "externalId", externalId));
        return bikeInspectionItemStatusMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeInspectionItemStatusDTO getByName(String name) {
        BikeInspectionItemStatus entity = bikeInspectionItemStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemStatus", "name", name));
        return bikeInspectionItemStatusMapper.toDto(entity);
    }

    @Transactional
    public BikeInspectionItemStatusDTO create(BikeInspectionItemStatusDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bike inspection item statuses");
        }

        BikeInspectionItemStatus entity = bikeInspectionItemStatusMapper.toEntity(dto);
        entity.sanitizeForCreate();
        entity = bikeInspectionItemStatusRepository.save(entity);
        return bikeInspectionItemStatusMapper.toDto(entity);
    }

    @Transactional
    public BikeInspectionItemStatusDTO update(Long id, BikeInspectionItemStatusDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike inspection item statuses");
        }

        BikeInspectionItemStatus entity = bikeInspectionItemStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemStatus", "id", id));

        bikeInspectionItemStatusMapper.updateEntityFromDto(dto, entity);
        entity = bikeInspectionItemStatusRepository.save(entity);
        return bikeInspectionItemStatusMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike inspection item statuses");
        }

        BikeInspectionItemStatus entity = bikeInspectionItemStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemStatus", "id", id));
        bikeInspectionItemStatusRepository.delete(entity);
    }
}
