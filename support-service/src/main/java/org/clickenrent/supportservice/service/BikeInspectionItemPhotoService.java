package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemPhotoDTO;
import org.clickenrent.supportservice.entity.BikeInspectionItemPhoto;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeInspectionItemPhotoMapper;
import org.clickenrent.supportservice.repository.BikeInspectionItemPhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing BikeInspectionItemPhoto entities.
 */
@Service
@RequiredArgsConstructor
public class BikeInspectionItemPhotoService {

    private final BikeInspectionItemPhotoRepository bikeInspectionItemPhotoRepository;
    private final BikeInspectionItemPhotoMapper bikeInspectionItemPhotoMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeInspectionItemPhotoDTO> getAll() {
        return bikeInspectionItemPhotoRepository.findAll().stream()
                .map(bikeInspectionItemPhotoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BikeInspectionItemPhotoDTO getById(Long id) {
        BikeInspectionItemPhoto entity = bikeInspectionItemPhotoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemPhoto", "id", id));
        return bikeInspectionItemPhotoMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionItemPhotoDTO> getByBikeInspectionItemId(Long bikeInspectionItemId) {
        return bikeInspectionItemPhotoRepository.findByBikeInspectionItemId(bikeInspectionItemId).stream()
                .map(bikeInspectionItemPhotoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionItemPhotoDTO> getByCompanyExternalId(String companyExternalId) {
        return bikeInspectionItemPhotoRepository.findByCompanyExternalId(companyExternalId).stream()
                .map(bikeInspectionItemPhotoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BikeInspectionItemPhotoDTO create(BikeInspectionItemPhotoDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bike inspection item photos");
        }

        BikeInspectionItemPhoto entity = bikeInspectionItemPhotoMapper.toEntity(dto);
        entity = bikeInspectionItemPhotoRepository.save(entity);
        return bikeInspectionItemPhotoMapper.toDto(entity);
    }

    @Transactional
    public BikeInspectionItemPhotoDTO update(Long id, BikeInspectionItemPhotoDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike inspection item photos");
        }

        BikeInspectionItemPhoto entity = bikeInspectionItemPhotoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemPhoto", "id", id));

        bikeInspectionItemPhotoMapper.updateEntityFromDto(dto, entity);
        entity = bikeInspectionItemPhotoRepository.save(entity);
        return bikeInspectionItemPhotoMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike inspection item photos");
        }

        BikeInspectionItemPhoto entity = bikeInspectionItemPhotoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemPhoto", "id", id));
        bikeInspectionItemPhotoRepository.delete(entity);
    }
}
