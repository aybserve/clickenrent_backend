package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeStatusDTO;
import org.clickenrent.rentalservice.entity.BikeStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeStatusMapper;
import org.clickenrent.rentalservice.repository.BikeStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BikeStatusService {

    private final BikeStatusRepository bikeStatusRepository;
    private final BikeStatusMapper bikeStatusMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeStatusDTO> getAllBikeStatuses() {
        return bikeStatusRepository.findAll().stream()
                .map(bikeStatusMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BikeStatusDTO getBikeStatusById(Long id) {
        BikeStatus bikeStatus = bikeStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeStatus", "id", id));
        return bikeStatusMapper.toDto(bikeStatus);
    }

    @Transactional
    public BikeStatusDTO createBikeStatus(BikeStatusDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bike statuses");
        }

        BikeStatus bikeStatus = bikeStatusMapper.toEntity(dto);
        bikeStatus = bikeStatusRepository.save(bikeStatus);
        return bikeStatusMapper.toDto(bikeStatus);
    }

    @Transactional
    public BikeStatusDTO updateBikeStatus(Long id, BikeStatusDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike statuses");
        }

        BikeStatus bikeStatus = bikeStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeStatus", "id", id));

        bikeStatusMapper.updateEntityFromDto(dto, bikeStatus);
        bikeStatus = bikeStatusRepository.save(bikeStatus);
        return bikeStatusMapper.toDto(bikeStatus);
    }

    @Transactional
    public void deleteBikeStatus(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike statuses");
        }

        BikeStatus bikeStatus = bikeStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeStatus", "id", id));
        bikeStatusRepository.delete(bikeStatus);
    }
}







