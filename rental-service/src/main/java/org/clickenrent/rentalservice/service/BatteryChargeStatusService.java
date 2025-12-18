package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BatteryChargeStatusDTO;
import org.clickenrent.rentalservice.entity.BatteryChargeStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BatteryChargeStatusMapper;
import org.clickenrent.rentalservice.repository.BatteryChargeStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BatteryChargeStatusService {

    private final BatteryChargeStatusRepository batteryChargeStatusRepository;
    private final BatteryChargeStatusMapper batteryChargeStatusMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BatteryChargeStatusDTO> getAllStatuses() {
        return batteryChargeStatusRepository.findAll().stream()
                .map(batteryChargeStatusMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BatteryChargeStatusDTO getStatusById(Long id) {
        BatteryChargeStatus status = batteryChargeStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BatteryChargeStatus", "id", id));
        return batteryChargeStatusMapper.toDto(status);
    }

    @Transactional
    public BatteryChargeStatusDTO createStatus(BatteryChargeStatusDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create battery charge statuses");
        }

        BatteryChargeStatus status = batteryChargeStatusMapper.toEntity(dto);
        status = batteryChargeStatusRepository.save(status);
        return batteryChargeStatusMapper.toDto(status);
    }

    @Transactional
    public BatteryChargeStatusDTO updateStatus(Long id, BatteryChargeStatusDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update battery charge statuses");
        }

        BatteryChargeStatus status = batteryChargeStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BatteryChargeStatus", "id", id));

        batteryChargeStatusMapper.updateEntityFromDto(dto, status);
        status = batteryChargeStatusRepository.save(status);
        return batteryChargeStatusMapper.toDto(status);
    }

    @Transactional
    public void deleteStatus(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete battery charge statuses");
        }

        BatteryChargeStatus status = batteryChargeStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BatteryChargeStatus", "id", id));
        batteryChargeStatusRepository.delete(status);
    }
}

