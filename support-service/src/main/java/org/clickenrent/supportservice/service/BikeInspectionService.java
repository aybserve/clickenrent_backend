package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionDTO;
import org.clickenrent.supportservice.entity.BikeInspection;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeInspectionMapper;
import org.clickenrent.supportservice.repository.BikeInspectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing BikeInspection entities.
 */
@Service
@RequiredArgsConstructor
public class BikeInspectionService {

    private final BikeInspectionRepository bikeInspectionRepository;
    private final BikeInspectionMapper bikeInspectionMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeInspectionDTO> getAll() {
        return bikeInspectionRepository.findAll().stream()
                .map(bikeInspectionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BikeInspectionDTO getById(Long id) {
        BikeInspection entity = bikeInspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspection", "id", id));
        return bikeInspectionMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeInspectionDTO getByExternalId(String externalId) {
        BikeInspection entity = bikeInspectionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspection", "externalId", externalId));
        return bikeInspectionMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionDTO> getByUserExternalId(String userExternalId) {
        return bikeInspectionRepository.findByUserExternalId(userExternalId).stream()
                .map(bikeInspectionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionDTO> getByCompanyExternalId(String companyExternalId) {
        return bikeInspectionRepository.findByCompanyExternalId(companyExternalId).stream()
                .map(bikeInspectionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionDTO> getByStatusId(Long statusId) {
        return bikeInspectionRepository.findByBikeInspectionStatusId(statusId).stream()
                .map(bikeInspectionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BikeInspectionDTO create(BikeInspectionDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bike inspections");
        }

        BikeInspection entity = bikeInspectionMapper.toEntity(dto);
        entity.sanitizeForCreate();
        entity = bikeInspectionRepository.save(entity);
        return bikeInspectionMapper.toDto(entity);
    }

    @Transactional
    public BikeInspectionDTO update(Long id, BikeInspectionDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike inspections");
        }

        BikeInspection entity = bikeInspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspection", "id", id));

        bikeInspectionMapper.updateEntityFromDto(dto, entity);
        entity = bikeInspectionRepository.save(entity);
        return bikeInspectionMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike inspections");
        }

        BikeInspection entity = bikeInspectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspection", "id", id));
        bikeInspectionRepository.delete(entity);
    }
}
