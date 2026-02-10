package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeModelRentalPlanDTO;
import org.clickenrent.rentalservice.entity.BikeModel;
import org.clickenrent.rentalservice.entity.BikeModelRentalPlan;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeModelRentalPlanMapper;
import org.clickenrent.rentalservice.repository.BikeModelRentalPlanRepository;
import org.clickenrent.rentalservice.repository.BikeModelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BikeModelRentalPlanService {

    private final BikeModelRentalPlanRepository bikeModelRentalPlanRepository;
    private final BikeModelRepository bikeModelRepository;
    private final BikeModelRentalPlanMapper bikeModelRentalPlanMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeModelRentalPlanDTO> getPlansByBikeModel(Long bikeModelId) {
        BikeModel bikeModel = bikeModelRepository.findById(bikeModelId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeModel", "id", bikeModelId));

        return bikeModelRentalPlanRepository.findByBikeModel(bikeModel).stream()
                .map(bikeModelRentalPlanMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BikeModelRentalPlanDTO getById(Long id) {
        BikeModelRentalPlan plan = bikeModelRentalPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeModelRentalPlan", "id", id));
        return bikeModelRentalPlanMapper.toDto(plan);
    }

    @Transactional
    public BikeModelRentalPlanDTO create(BikeModelRentalPlanDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bike model rental plans");
        }

        BikeModelRentalPlan plan = bikeModelRentalPlanMapper.toEntity(dto);
        plan.sanitizeForCreate();
        plan = bikeModelRentalPlanRepository.save(plan);
        return bikeModelRentalPlanMapper.toDto(plan);
    }

    @Transactional
    public BikeModelRentalPlanDTO update(Long id, BikeModelRentalPlanDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike model rental plans");
        }

        BikeModelRentalPlan plan = bikeModelRentalPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeModelRentalPlan", "id", id));

        bikeModelRentalPlanMapper.updateEntityFromDto(dto, plan);
        plan = bikeModelRentalPlanRepository.save(plan);
        return bikeModelRentalPlanMapper.toDto(plan);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike model rental plans");
        }

        BikeModelRentalPlan plan = bikeModelRentalPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeModelRentalPlan", "id", id));
        bikeModelRentalPlanRepository.delete(plan);
    }
}








