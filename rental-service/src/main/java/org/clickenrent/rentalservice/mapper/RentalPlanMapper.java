package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.RentalPlanDTO;
import org.clickenrent.rentalservice.entity.RentalPlan;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.clickenrent.rentalservice.repository.RentalUnitRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between RentalPlan entity and RentalPlanDTO.
 */
@Component
@RequiredArgsConstructor
public class RentalPlanMapper {

    private final RentalUnitRepository rentalUnitRepository;
    private final LocationRepository locationRepository;

    public RentalPlanDTO toDto(RentalPlan rentalPlan) {
        if (rentalPlan == null) {
            return null;
        }

        return RentalPlanDTO.builder()
                .id(rentalPlan.getId())
                .name(rentalPlan.getName())
                .rentalUnitId(rentalPlan.getRentalUnit() != null ? rentalPlan.getRentalUnit().getId() : null)
                .minUnit(rentalPlan.getMinUnit())
                .maxUnit(rentalPlan.getMaxUnit())
                .locationId(rentalPlan.getLocation() != null ? rentalPlan.getLocation().getId() : null)
                .build();
    }

    public RentalPlan toEntity(RentalPlanDTO dto) {
        if (dto == null) {
            return null;
        }

        RentalPlan.RentalPlanBuilder builder = RentalPlan.builder()
                .id(dto.getId())
                .name(dto.getName())
                .minUnit(dto.getMinUnit())
                .maxUnit(dto.getMaxUnit());

        if (dto.getRentalUnitId() != null) {
            builder.rentalUnit(rentalUnitRepository.findById(dto.getRentalUnitId()).orElse(null));
        }
        if (dto.getLocationId() != null) {
            builder.location(locationRepository.findById(dto.getLocationId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(RentalPlanDTO dto, RentalPlan rentalPlan) {
        if (dto == null || rentalPlan == null) {
            return;
        }

        if (dto.getName() != null) {
            rentalPlan.setName(dto.getName());
        }
        if (dto.getMinUnit() != null) {
            rentalPlan.setMinUnit(dto.getMinUnit());
        }
        if (dto.getMaxUnit() != null) {
            rentalPlan.setMaxUnit(dto.getMaxUnit());
        }
    }
}
