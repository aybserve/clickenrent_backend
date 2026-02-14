package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.ChargingStationModelDTO;
import org.clickenrent.rentalservice.entity.ChargingStationModel;
import org.clickenrent.rentalservice.repository.ChargingStationBrandRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between ChargingStationModel entity and ChargingStationModelDTO.
 */
@Component
@RequiredArgsConstructor
public class ChargingStationModelMapper {

    private final ChargingStationBrandRepository chargingStationBrandRepository;

    public ChargingStationModelDTO toDto(ChargingStationModel model) {
        if (model == null) {
            return null;
        }

        return ChargingStationModelDTO.builder()
                .id(model.getId())
                .externalId(model.getExternalId())
                .name(model.getName())
                .chargingStationBrandId(model.getChargingStationBrand() != null ? 
                        model.getChargingStationBrand().getId() : null)
                .imageUrl(model.getImageUrl())
                .b2bSalePrice(model.getB2bSalePrice())
                .b2bSubscriptionPrice(model.getB2bSubscriptionPrice())
                .dateCreated(model.getDateCreated())
                .lastDateModified(model.getLastDateModified())
                .createdBy(model.getCreatedBy())
                .lastModifiedBy(model.getLastModifiedBy())
                .build();
    }

    public ChargingStationModel toEntity(ChargingStationModelDTO dto) {
        if (dto == null) {
            return null;
        }

        ChargingStationModel.ChargingStationModelBuilder builder = ChargingStationModel.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .imageUrl(dto.getImageUrl())
                .b2bSalePrice(dto.getB2bSalePrice())
                .b2bSubscriptionPrice(dto.getB2bSubscriptionPrice())
                .dateCreated(dto.getDateCreated())
                .lastDateModified(dto.getLastDateModified())
                .createdBy(dto.getCreatedBy())
                .lastModifiedBy(dto.getLastModifiedBy());

        if (dto.getChargingStationBrandId() != null) {
            builder.chargingStationBrand(chargingStationBrandRepository.findById(dto.getChargingStationBrandId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(ChargingStationModelDTO dto, ChargingStationModel model) {
        if (dto == null || model == null) {
            return;
        }

        if (dto.getName() != null) {
            model.setName(dto.getName());
        }
        if (dto.getImageUrl() != null) {
            model.setImageUrl(dto.getImageUrl());
        }
        if (dto.getB2bSalePrice() != null) {
            model.setB2bSalePrice(dto.getB2bSalePrice());
        }
        if (dto.getB2bSubscriptionPrice() != null) {
            model.setB2bSubscriptionPrice(dto.getB2bSubscriptionPrice());
        }
    }
}
