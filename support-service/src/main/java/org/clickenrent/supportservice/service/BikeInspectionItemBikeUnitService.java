package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemBikeUnitDTO;
import org.clickenrent.supportservice.entity.BikeInspectionItemBikeUnit;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeInspectionItemBikeUnitMapper;
import org.clickenrent.supportservice.repository.BikeInspectionItemBikeUnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing BikeInspectionItemBikeUnit entities.
 */
@Service
@RequiredArgsConstructor
public class BikeInspectionItemBikeUnitService {

    private final BikeInspectionItemBikeUnitRepository bikeInspectionItemBikeUnitRepository;
    private final BikeInspectionItemBikeUnitMapper bikeInspectionItemBikeUnitMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeInspectionItemBikeUnitDTO> getAll() {
        return bikeInspectionItemBikeUnitRepository.findAll().stream()
                .map(bikeInspectionItemBikeUnitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BikeInspectionItemBikeUnitDTO getById(Long id) {
        BikeInspectionItemBikeUnit entity = bikeInspectionItemBikeUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemBikeUnit", "id", id));
        return bikeInspectionItemBikeUnitMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeInspectionItemBikeUnitDTO getByExternalId(String externalId) {
        BikeInspectionItemBikeUnit entity = bikeInspectionItemBikeUnitRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemBikeUnit", "externalId", externalId));
        return bikeInspectionItemBikeUnitMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionItemBikeUnitDTO> getByBikeInspectionItemId(Long bikeInspectionItemId) {
        return bikeInspectionItemBikeUnitRepository.findByBikeInspectionItemId(bikeInspectionItemId).stream()
                .map(bikeInspectionItemBikeUnitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionItemBikeUnitDTO> getByBikeUnitId(Long bikeUnitId) {
        return bikeInspectionItemBikeUnitRepository.findByBikeUnitId(bikeUnitId).stream()
                .map(bikeInspectionItemBikeUnitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionItemBikeUnitDTO> getByCompanyExternalId(String companyExternalId) {
        return bikeInspectionItemBikeUnitRepository.findByCompanyExternalId(companyExternalId).stream()
                .map(bikeInspectionItemBikeUnitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BikeInspectionItemBikeUnitDTO create(BikeInspectionItemBikeUnitDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bike inspection item bike unit links");
        }

        BikeInspectionItemBikeUnit entity = bikeInspectionItemBikeUnitMapper.toEntity(dto);
        entity.sanitizeForCreate();
        entity = bikeInspectionItemBikeUnitRepository.save(entity);
        return bikeInspectionItemBikeUnitMapper.toDto(entity);
    }

    @Transactional
    public BikeInspectionItemBikeUnitDTO update(Long id, BikeInspectionItemBikeUnitDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike inspection item bike unit links");
        }

        BikeInspectionItemBikeUnit entity = bikeInspectionItemBikeUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemBikeUnit", "id", id));

        bikeInspectionItemBikeUnitMapper.updateEntityFromDto(dto, entity);
        entity = bikeInspectionItemBikeUnitRepository.save(entity);
        return bikeInspectionItemBikeUnitMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike inspection item bike unit links");
        }

        BikeInspectionItemBikeUnit entity = bikeInspectionItemBikeUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemBikeUnit", "id", id));
        bikeInspectionItemBikeUnitRepository.delete(entity);
    }
}
