package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeTypeDTO;
import org.clickenrent.rentalservice.entity.BikeType;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeTypeMapper;
import org.clickenrent.rentalservice.repository.BikeTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BikeTypeService {

    private final BikeTypeRepository bikeTypeRepository;
    private final BikeTypeMapper bikeTypeMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeTypeDTO> getAllBikeTypes() {
        return bikeTypeRepository.findAll().stream()
                .map(bikeTypeMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BikeTypeDTO getBikeTypeById(Long id) {
        BikeType bikeType = bikeTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeType", "id", id));
        return bikeTypeMapper.toDto(bikeType);
    }

    @Transactional(readOnly = true)
    public BikeTypeDTO getBikeTypeByExternalId(String externalId) {
        BikeType bikeType = bikeTypeRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeType", "externalId", externalId));
        return bikeTypeMapper.toDto(bikeType);
    }

    @Transactional
    public BikeTypeDTO createBikeType(BikeTypeDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bike types");
        }

        BikeType bikeType = bikeTypeMapper.toEntity(dto);
        bikeType.sanitizeForCreate();
        bikeType = bikeTypeRepository.save(bikeType);
        return bikeTypeMapper.toDto(bikeType);
    }

    @Transactional
    public BikeTypeDTO updateBikeType(Long id, BikeTypeDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike types");
        }

        BikeType bikeType = bikeTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeType", "id", id));

        bikeTypeMapper.updateEntityFromDto(dto, bikeType);
        bikeType = bikeTypeRepository.save(bikeType);
        return bikeTypeMapper.toDto(bikeType);
    }

    @Transactional
    public void deleteBikeType(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike types");
        }

        BikeType bikeType = bikeTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeType", "id", id));
        bikeTypeRepository.delete(bikeType);
    }
}








