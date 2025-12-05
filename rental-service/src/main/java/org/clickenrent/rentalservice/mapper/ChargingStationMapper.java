package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.ChargingStationDTO;
import org.clickenrent.rentalservice.entity.ChargingStation;
import org.clickenrent.rentalservice.repository.*;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between ChargingStation entity and ChargingStationDTO.
 */
@Component
@RequiredArgsConstructor
public class ChargingStationMapper {

    private final ChargingStationStatusRepository chargingStationStatusRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final HubRepository hubRepository;
    private final ChargingStationModelRepository chargingStationModelRepository;

    public ChargingStationDTO toDto(ChargingStation chargingStation) {
        if (chargingStation == null) {
            return null;
        }

        return ChargingStationDTO.builder()
                .id(chargingStation.getId())
                .externalId(chargingStation.getExternalId())
                .chargingStationStatusId(chargingStation.getChargingStationStatus() != null ? 
                        chargingStation.getChargingStationStatus().getId() : null)
                .coordinatesId(chargingStation.getCoordinates() != null ? chargingStation.getCoordinates().getId() : null)
                .isActive(chargingStation.getIsActive())
                .hubId(chargingStation.getHub() != null ? chargingStation.getHub().getId() : null)
                .inServiceDate(chargingStation.getInServiceDate())
                .chargingStationModelId(chargingStation.getChargingStationModel() != null ? 
                        chargingStation.getChargingStationModel().getId() : null)
                .isB2BRentable(chargingStation.getIsB2BRentable())
                .dateCreated(chargingStation.getDateCreated())
                .lastDateModified(chargingStation.getLastDateModified())
                .createdBy(chargingStation.getCreatedBy())
                .lastModifiedBy(chargingStation.getLastModifiedBy())
                .build();
    }

    public ChargingStation toEntity(ChargingStationDTO dto) {
        if (dto == null) {
            return null;
        }

        ChargingStation.ChargingStationBuilder<?, ?> builder = ChargingStation.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .isActive(dto.getIsActive())
                .inServiceDate(dto.getInServiceDate())
                .isB2BRentable(dto.getIsB2BRentable());

        if (dto.getChargingStationStatusId() != null) {
            builder.chargingStationStatus(chargingStationStatusRepository.findById(dto.getChargingStationStatusId()).orElse(null));
        }
        if (dto.getCoordinatesId() != null) {
            builder.coordinates(coordinatesRepository.findById(dto.getCoordinatesId()).orElse(null));
        }
        if (dto.getHubId() != null) {
            builder.hub(hubRepository.findById(dto.getHubId()).orElse(null));
        }
        if (dto.getChargingStationModelId() != null) {
            builder.chargingStationModel(chargingStationModelRepository.findById(dto.getChargingStationModelId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(ChargingStationDTO dto, ChargingStation chargingStation) {
        if (dto == null || chargingStation == null) {
            return;
        }

        if (dto.getIsActive() != null) {
            chargingStation.setIsActive(dto.getIsActive());
        }
        if (dto.getInServiceDate() != null) {
            chargingStation.setInServiceDate(dto.getInServiceDate());
        }
    }
}
