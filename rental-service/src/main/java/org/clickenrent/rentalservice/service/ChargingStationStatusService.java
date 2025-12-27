package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.ChargingStationStatusDTO;
import org.clickenrent.rentalservice.entity.ChargingStationStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.ChargingStationStatusMapper;
import org.clickenrent.rentalservice.repository.ChargingStationStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargingStationStatusService {

    private final ChargingStationStatusRepository chargingStationStatusRepository;
    private final ChargingStationStatusMapper chargingStationStatusMapper;

    @Transactional(readOnly = true)
    public List<ChargingStationStatusDTO> getAllStatuses() {
        return chargingStationStatusRepository.findAll().stream()
                .map(chargingStationStatusMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChargingStationStatusDTO getStatusById(Long id) {
        ChargingStationStatus status = chargingStationStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChargingStationStatus", "id", id));
        return chargingStationStatusMapper.toDto(status);
    }
}







