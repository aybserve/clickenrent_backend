package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeRentalStatusDTO;
import org.clickenrent.rentalservice.entity.BikeRentalStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.BikeRentalStatusMapper;
import org.clickenrent.rentalservice.repository.BikeRentalStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BikeRentalStatusService {

    private final BikeRentalStatusRepository bikeRentalStatusRepository;
    private final BikeRentalStatusMapper bikeRentalStatusMapper;

    @Transactional(readOnly = true)
    public List<BikeRentalStatusDTO> getAllStatuses() {
        return bikeRentalStatusRepository.findAll().stream()
                .map(bikeRentalStatusMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BikeRentalStatusDTO getStatusById(Long id) {
        BikeRentalStatus status = bikeRentalStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRentalStatus", "id", id));
        return bikeRentalStatusMapper.toDto(status);
    }
}








