package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.RentalPlanDTO;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.entity.RentalPlan;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.RentalPlanMapper;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.clickenrent.rentalservice.repository.RentalPlanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalPlanService {

    private final RentalPlanRepository rentalPlanRepository;
    private final LocationRepository locationRepository;
    private final RentalPlanMapper rentalPlanMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<RentalPlanDTO> getAllRentalPlans(Pageable pageable) {
        return rentalPlanRepository.findAll(pageable)
                .map(rentalPlanMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<RentalPlanDTO> getRentalPlansByLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", locationId));

        return rentalPlanRepository.findByLocation(location).stream()
                .map(rentalPlanMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RentalPlanDTO getRentalPlanById(Long id) {
        RentalPlan rentalPlan = rentalPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalPlan", "id", id));
        return rentalPlanMapper.toDto(rentalPlan);
    }

    @Transactional
    public RentalPlanDTO createRentalPlan(RentalPlanDTO dto) {
        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", dto.getLocationId()));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(location.getCompanyId())) {
            throw new UnauthorizedException("You don't have permission to create rental plans for this location");
        }

        RentalPlan rentalPlan = rentalPlanMapper.toEntity(dto);
        rentalPlan = rentalPlanRepository.save(rentalPlan);
        return rentalPlanMapper.toDto(rentalPlan);
    }

    @Transactional
    public RentalPlanDTO updateRentalPlan(Long id, RentalPlanDTO dto) {
        RentalPlan rentalPlan = rentalPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalPlan", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(rentalPlan.getLocation().getCompanyId())) {
            throw new UnauthorizedException("You don't have permission to update this rental plan");
        }

        rentalPlanMapper.updateEntityFromDto(dto, rentalPlan);
        rentalPlan = rentalPlanRepository.save(rentalPlan);
        return rentalPlanMapper.toDto(rentalPlan);
    }

    @Transactional
    public void deleteRentalPlan(Long id) {
        RentalPlan rentalPlan = rentalPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalPlan", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete rental plans");
        }

        rentalPlanRepository.delete(rentalPlan);
    }
}
