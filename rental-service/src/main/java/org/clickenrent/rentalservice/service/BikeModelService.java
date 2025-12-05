package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeModelDTO;
import org.clickenrent.rentalservice.entity.BikeModel;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeModelMapper;
import org.clickenrent.rentalservice.repository.BikeModelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BikeModelService {

    private final BikeModelRepository bikeModelRepository;
    private final BikeModelMapper bikeModelMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<BikeModelDTO> getAllBikeModels(Pageable pageable) {
        return bikeModelRepository.findAll(pageable)
                .map(bikeModelMapper::toDto);
    }

    @Transactional(readOnly = true)
    public BikeModelDTO getBikeModelById(Long id) {
        BikeModel bikeModel = bikeModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeModel", "id", id));
        return bikeModelMapper.toDto(bikeModel);
    }

    @Transactional
    public BikeModelDTO createBikeModel(BikeModelDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bike models");
        }

        BikeModel bikeModel = bikeModelMapper.toEntity(dto);
        bikeModel = bikeModelRepository.save(bikeModel);
        return bikeModelMapper.toDto(bikeModel);
    }

    @Transactional
    public BikeModelDTO updateBikeModel(Long id, BikeModelDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike models");
        }

        BikeModel bikeModel = bikeModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeModel", "id", id));

        bikeModelMapper.updateEntityFromDto(dto, bikeModel);
        bikeModel = bikeModelRepository.save(bikeModel);
        return bikeModelMapper.toDto(bikeModel);
    }

    @Transactional
    public void deleteBikeModel(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike models");
        }

        BikeModel bikeModel = bikeModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeModel", "id", id));
        bikeModelRepository.delete(bikeModel);
    }
}
