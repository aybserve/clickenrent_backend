package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeModelDTO;
import org.clickenrent.rentalservice.entity.BikeModel;
import org.clickenrent.rentalservice.repository.BikeBrandRepository;
import org.clickenrent.rentalservice.repository.BikeEngineRepository;
import org.clickenrent.rentalservice.repository.BikeTypeRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeModel entity and BikeModelDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeModelMapper {

    private final BikeBrandRepository bikeBrandRepository;
    private final BikeTypeRepository bikeTypeRepository;
    private final BikeEngineRepository bikeEngineRepository;

    public BikeModelDTO toDto(BikeModel bikeModel) {
        if (bikeModel == null) {
            return null;
        }

        return BikeModelDTO.builder()
                .id(bikeModel.getId())
                .externalId(bikeModel.getExternalId())
                .name(bikeModel.getName())
                .bikeBrandId(bikeModel.getBikeBrand() != null ? bikeModel.getBikeBrand().getId() : null)
                .imageUrl(bikeModel.getImageUrl())
                .bikeTypeId(bikeModel.getBikeType() != null ? bikeModel.getBikeType().getId() : null)
                .bikeEngineId(bikeModel.getBikeEngine() != null ? bikeModel.getBikeEngine().getId() : null)
                .b2bSalePrice(bikeModel.getB2bSalePrice())
                .b2bSubscriptionPrice(bikeModel.getB2bSubscriptionPrice())
                .build();
    }

    public BikeModel toEntity(BikeModelDTO dto) {
        if (dto == null) {
            return null;
        }

        BikeModel.BikeModelBuilder builder = BikeModel.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .imageUrl(dto.getImageUrl())
                .b2bSalePrice(dto.getB2bSalePrice())
                .b2bSubscriptionPrice(dto.getB2bSubscriptionPrice());

        if (dto.getBikeBrandId() != null) {
            builder.bikeBrand(bikeBrandRepository.findById(dto.getBikeBrandId()).orElse(null));
        }
        if (dto.getBikeTypeId() != null) {
            builder.bikeType(bikeTypeRepository.findById(dto.getBikeTypeId()).orElse(null));
        }
        if (dto.getBikeEngineId() != null) {
            builder.bikeEngine(bikeEngineRepository.findById(dto.getBikeEngineId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(BikeModelDTO dto, BikeModel bikeModel) {
        if (dto == null || bikeModel == null) {
            return;
        }

        if (dto.getName() != null) {
            bikeModel.setName(dto.getName());
        }
        if (dto.getImageUrl() != null) {
            bikeModel.setImageUrl(dto.getImageUrl());
        }
        if (dto.getB2bSalePrice() != null) {
            bikeModel.setB2bSalePrice(dto.getB2bSalePrice());
        }
        if (dto.getB2bSubscriptionPrice() != null) {
            bikeModel.setB2bSubscriptionPrice(dto.getB2bSubscriptionPrice());
        }
    }
}
