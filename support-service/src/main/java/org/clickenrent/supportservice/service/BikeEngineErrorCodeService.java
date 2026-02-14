package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeEngineErrorCodeDTO;
import org.clickenrent.supportservice.entity.BikeEngineErrorCode;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeEngineErrorCodeMapper;
import org.clickenrent.supportservice.repository.BikeEngineErrorCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing BikeEngineErrorCode junction entities.
 */
@Service
@RequiredArgsConstructor
public class BikeEngineErrorCodeService {

    private final BikeEngineErrorCodeRepository bikeEngineErrorCodeRepository;
    private final BikeEngineErrorCodeMapper bikeEngineErrorCodeMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeEngineErrorCodeDTO> getAll() {
        return bikeEngineErrorCodeRepository.findAll().stream()
                .map(bikeEngineErrorCodeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BikeEngineErrorCodeDTO getById(Long id) {
        BikeEngineErrorCode entity = bikeEngineErrorCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeEngineErrorCode", "id", id));
        return bikeEngineErrorCodeMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeEngineErrorCodeDTO getByExternalId(String externalId) {
        BikeEngineErrorCode entity = bikeEngineErrorCodeRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeEngineErrorCode", "externalId", externalId));
        return bikeEngineErrorCodeMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<BikeEngineErrorCodeDTO> getByBikeEngineExternalId(String bikeEngineExternalId) {
        return bikeEngineErrorCodeRepository.findByBikeEngineExternalId(bikeEngineExternalId).stream()
                .map(bikeEngineErrorCodeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeEngineErrorCodeDTO> getByErrorCodeId(Long errorCodeId) {
        return bikeEngineErrorCodeRepository.findByErrorCodeId(errorCodeId).stream()
                .map(bikeEngineErrorCodeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BikeEngineErrorCodeDTO create(BikeEngineErrorCodeDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can link bike engines to error codes");
        }

        BikeEngineErrorCode entity = bikeEngineErrorCodeMapper.toEntity(dto);
        entity.sanitizeForCreate();
        entity = bikeEngineErrorCodeRepository.save(entity);
        return bikeEngineErrorCodeMapper.toDto(entity);
    }

    @Transactional
    public BikeEngineErrorCodeDTO update(Long id, BikeEngineErrorCodeDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike engine error code links");
        }

        BikeEngineErrorCode entity = bikeEngineErrorCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeEngineErrorCode", "id", id));

        bikeEngineErrorCodeMapper.updateEntityFromDto(dto, entity);
        entity = bikeEngineErrorCodeRepository.save(entity);
        return bikeEngineErrorCodeMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike engine error code links");
        }

        BikeEngineErrorCode entity = bikeEngineErrorCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeEngineErrorCode", "id", id));
        bikeEngineErrorCodeRepository.delete(entity);
    }
}
