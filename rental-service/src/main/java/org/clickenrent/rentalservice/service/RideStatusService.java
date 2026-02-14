package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.RideStatusDTO;
import org.clickenrent.rentalservice.entity.RideStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.RideStatusMapper;
import org.clickenrent.rentalservice.repository.RideStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideStatusService {

    private final RideStatusRepository rideStatusRepository;
    private final RideStatusMapper rideStatusMapper;

    @Transactional(readOnly = true)
    public List<RideStatusDTO> getAllStatuses() {
        return rideStatusRepository.findAll().stream()
                .map(rideStatusMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RideStatusDTO getStatusById(Long id) {
        RideStatus status = rideStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RideStatus", "id", id));
        return rideStatusMapper.toDto(status);
    }

    @Transactional(readOnly = true)
    public RideStatusDTO findByExternalId(String externalId) {
        RideStatus status = rideStatusRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("RideStatus", "externalId", externalId));
        return rideStatusMapper.toDto(status);
    }
}








