package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeUnitDTO;
import org.clickenrent.supportservice.entity.BikeUnit;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeUnitMapper;
import org.clickenrent.supportservice.repository.BikeUnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing BikeUnit entities.
 */
@Service
@RequiredArgsConstructor
public class BikeUnitService {

    private final BikeUnitRepository bikeUnitRepository;
    private final BikeUnitMapper bikeUnitMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeUnitDTO> getAll() {
        return bikeUnitRepository.findAll().stream()
                .map(bikeUnitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BikeUnitDTO getById(Long id) {
        BikeUnit entity = bikeUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeUnit", "id", id));
        return bikeUnitMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeUnitDTO getByExternalId(String externalId) {
        BikeUnit entity = bikeUnitRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeUnit", "externalId", externalId));
        return bikeUnitMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<BikeUnitDTO> getByCompanyExternalId(String companyExternalId) {
        return bikeUnitRepository.findByCompanyExternalId(companyExternalId).stream()
                .map(bikeUnitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BikeUnitDTO create(BikeUnitDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bike units");
        }

        BikeUnit entity = bikeUnitMapper.toEntity(dto);
        entity.sanitizeForCreate();
        entity = bikeUnitRepository.save(entity);
        return bikeUnitMapper.toDto(entity);
    }

    @Transactional
    public BikeUnitDTO update(Long id, BikeUnitDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike units");
        }

        BikeUnit entity = bikeUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeUnit", "id", id));

        bikeUnitMapper.updateEntityFromDto(dto, entity);
        entity = bikeUnitRepository.save(entity);
        return bikeUnitMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike units");
        }

        BikeUnit entity = bikeUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeUnit", "id", id));
        bikeUnitRepository.delete(entity);
    }
}
