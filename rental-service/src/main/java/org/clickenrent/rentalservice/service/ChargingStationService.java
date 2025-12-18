package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.ChargingStationDTO;
import org.clickenrent.rentalservice.entity.ChargingStation;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.ChargingStationMapper;
import org.clickenrent.rentalservice.repository.ChargingStationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChargingStationService {

    private final ChargingStationRepository chargingStationRepository;
    private final ChargingStationMapper chargingStationMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<ChargingStationDTO> getAllChargingStations(Pageable pageable) {
        if (securityService.isAdmin()) {
            return chargingStationRepository.findAll(pageable)
                    .map(chargingStationMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all charging stations");
    }

    @Transactional(readOnly = true)
    public ChargingStationDTO getChargingStationById(Long id) {
        ChargingStation chargingStation = chargingStationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChargingStation", "id", id));
        return chargingStationMapper.toDto(chargingStation);
    }

    @Transactional
    public ChargingStationDTO createChargingStation(ChargingStationDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create charging stations");
        }

        ChargingStation chargingStation = chargingStationMapper.toEntity(dto);
        chargingStation = chargingStationRepository.save(chargingStation);
        return chargingStationMapper.toDto(chargingStation);
    }

    @Transactional
    public ChargingStationDTO updateChargingStation(Long id, ChargingStationDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update charging stations");
        }

        ChargingStation chargingStation = chargingStationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChargingStation", "id", id));

        chargingStationMapper.updateEntityFromDto(dto, chargingStation);
        chargingStation = chargingStationRepository.save(chargingStation);
        return chargingStationMapper.toDto(chargingStation);
    }

    @Transactional
    public void deleteChargingStation(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete charging stations");
        }

        ChargingStation chargingStation = chargingStationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChargingStation", "id", id));
        chargingStationRepository.delete(chargingStation);
    }
}

