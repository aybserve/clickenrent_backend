package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeModelRentalPlanDTO;
import org.clickenrent.rentalservice.entity.BikeModelRentalPlan;
import org.clickenrent.rentalservice.repository.BikeModelRepository;
import org.clickenrent.rentalservice.repository.RentalPlanRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeModelRentalPlan entity and BikeModelRentalPlanDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeModelRentalPlanMapper {

    private final BikeModelRepository bikeModelRepository;
    private final RentalPlanRepository rentalPlanRepository;

    public BikeModelRentalPlanDTO toDto(BikeModelRentalPlan bikeModelRentalPlan) {
        if (bikeModelRentalPlan == null) {
            return null;
        }

        return BikeModelRentalPlanDTO.builder()
                .id(bikeModelRentalPlan.getId())
                .bikeModelId(bikeModelRentalPlan.getBikeModel() != null ? bikeModelRentalPlan.getBikeModel().getId() : null)
                .rentalPlanId(bikeModelRentalPlan.getRentalPlan() != null ? bikeModelRentalPlan.getRentalPlan().getId() : null)
                .price(bikeModelRentalPlan.getPrice())
                .build();
    }

    public BikeModelRentalPlan toEntity(BikeModelRentalPlanDTO dto) {
        if (dto == null) {
            return null;
        }

        BikeModelRentalPlan.BikeModelRentalPlanBuilder builder = BikeModelRentalPlan.builder()
                .id(dto.getId())
                .price(dto.getPrice());

        if (dto.getBikeModelId() != null) {
            builder.bikeModel(bikeModelRepository.findById(dto.getBikeModelId()).orElse(null));
        }
        if (dto.getRentalPlanId() != null) {
            builder.rentalPlan(rentalPlanRepository.findById(dto.getRentalPlanId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(BikeModelRentalPlanDTO dto, BikeModelRentalPlan entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }
    }
}


