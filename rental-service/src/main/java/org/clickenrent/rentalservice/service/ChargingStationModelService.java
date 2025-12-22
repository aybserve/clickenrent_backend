package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.ChargingStationModelDTO;
import org.clickenrent.rentalservice.entity.ChargingStationModel;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.ChargingStationModelMapper;
import org.clickenrent.rentalservice.repository.ChargingStationModelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChargingStationModelService {

    private final ChargingStationModelRepository chargingStationModelRepository;
    private final ChargingStationModelMapper chargingStationModelMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<ChargingStationModelDTO> getAllModels(Pageable pageable) {
        return chargingStationModelRepository.findAll(pageable)
                .map(chargingStationModelMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ChargingStationModelDTO getModelById(Long id) {
        ChargingStationModel model = chargingStationModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChargingStationModel", "id", id));
        return chargingStationModelMapper.toDto(model);
    }

    @Transactional
    public ChargingStationModelDTO createModel(ChargingStationModelDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create charging station models");
        }

        ChargingStationModel model = chargingStationModelMapper.toEntity(dto);
        model = chargingStationModelRepository.save(model);
        return chargingStationModelMapper.toDto(model);
    }

    @Transactional
    public ChargingStationModelDTO updateModel(Long id, ChargingStationModelDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update charging station models");
        }

        ChargingStationModel model = chargingStationModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChargingStationModel", "id", id));

        chargingStationModelMapper.updateEntityFromDto(dto, model);
        model = chargingStationModelRepository.save(model);
        return chargingStationModelMapper.toDto(model);
    }

    @Transactional
    public void deleteModel(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete charging station models");
        }

        ChargingStationModel model = chargingStationModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChargingStationModel", "id", id));
        chargingStationModelRepository.delete(model);
    }
}




