package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemDTO;
import org.clickenrent.supportservice.entity.BikeInspectionItem;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeInspectionItemMapper;
import org.clickenrent.supportservice.repository.BikeInspectionItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing BikeInspectionItem entities.
 */
@Service
@RequiredArgsConstructor
public class BikeInspectionItemService {

    private final BikeInspectionItemRepository bikeInspectionItemRepository;
    private final BikeInspectionItemMapper bikeInspectionItemMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeInspectionItemDTO> getAll() {
        return bikeInspectionItemRepository.findAll().stream()
                .map(bikeInspectionItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BikeInspectionItemDTO getById(Long id) {
        BikeInspectionItem entity = bikeInspectionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItem", "id", id));
        return bikeInspectionItemMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeInspectionItemDTO getByExternalId(String externalId) {
        BikeInspectionItem entity = bikeInspectionItemRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItem", "externalId", externalId));
        return bikeInspectionItemMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionItemDTO> getByBikeInspectionId(Long bikeInspectionId) {
        return bikeInspectionItemRepository.findByBikeInspectionId(bikeInspectionId).stream()
                .map(bikeInspectionItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionItemDTO> getByBikeExternalId(String bikeExternalId) {
        return bikeInspectionItemRepository.findByBikeExternalId(bikeExternalId).stream()
                .map(bikeInspectionItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionItemDTO> getByCompanyExternalId(String companyExternalId) {
        return bikeInspectionItemRepository.findByCompanyExternalId(companyExternalId).stream()
                .map(bikeInspectionItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionItemDTO> getByStatusId(Long statusId) {
        return bikeInspectionItemRepository.findByBikeInspectionItemStatusId(statusId).stream()
                .map(bikeInspectionItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionItemDTO> getByErrorCodeId(Long errorCodeId) {
        return bikeInspectionItemRepository.findByErrorCodeId(errorCodeId).stream()
                .map(bikeInspectionItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BikeInspectionItemDTO create(BikeInspectionItemDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bike inspection items");
        }

        BikeInspectionItem entity = bikeInspectionItemMapper.toEntity(dto);
        entity = bikeInspectionItemRepository.save(entity);
        return bikeInspectionItemMapper.toDto(entity);
    }

    @Transactional
    public BikeInspectionItemDTO update(Long id, BikeInspectionItemDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike inspection items");
        }

        BikeInspectionItem entity = bikeInspectionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItem", "id", id));

        bikeInspectionItemMapper.updateEntityFromDto(dto, entity);
        entity = bikeInspectionItemRepository.save(entity);
        return bikeInspectionItemMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike inspection items");
        }

        BikeInspectionItem entity = bikeInspectionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItem", "id", id));
        bikeInspectionItemRepository.delete(entity);
    }
}
