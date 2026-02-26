package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeRentalDTO;
import org.clickenrent.rentalservice.entity.BikeRental;
import org.clickenrent.rentalservice.repository.*;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeRental entity and BikeRentalDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeRentalMapper {

    private final BikeRepository bikeRepository;
    private final LocationRepository locationRepository;
    private final RentalRepository rentalRepository;
    private final RentalUnitRepository rentalUnitRepository;
    private final BikeRentalStatusRepository bikeRentalStatusRepository;

    public BikeRentalDTO toDto(BikeRental bikeRental) {
        if (bikeRental == null) {
            return null;
        }

        return BikeRentalDTO.builder()
                .id(bikeRental.getId())
                .externalId(bikeRental.getExternalId())
                .bikeId(bikeRental.getBike() != null ? bikeRental.getBike().getId() : null)
                .locationId(bikeRental.getLocation() != null ? bikeRental.getLocation().getId() : null)
                .rentalId(bikeRental.getRental() != null ? bikeRental.getRental().getId() : null)
                .startDateTime(bikeRental.getStartDateTime())
                .endDateTime(bikeRental.getEndDateTime())
                .rentalUnitId(bikeRental.getRentalUnit() != null ? bikeRental.getRentalUnit().getId() : null)
                .bikeRentalStatusId(bikeRental.getBikeRentalStatus() != null ? bikeRental.getBikeRentalStatus().getId() : null)
                .bikeRentalStatusName(bikeRental.getBikeRentalStatus() != null ? bikeRental.getBikeRentalStatus().getName() : null)
                .bikeTypeName(bikeRental.getBike() != null && bikeRental.getBike().getBikeType() != null ? bikeRental.getBike().getBikeType().getName() : null)
                .locationName(bikeRental.getLocation() != null ? bikeRental.getLocation().getName() : null)
                .revenueSharePercent(bikeRental.getBike() != null ? bikeRental.getBike().getRevenueSharePercent() : null)
                .rentalExternalId(bikeRental.getRental() != null ? bikeRental.getRental().getExternalId() : null)
                .isRevenueSharePaid(bikeRental.getIsRevenueSharePaid())
                .photoUrl(bikeRental.getPhotoUrl())
                .price(bikeRental.getPrice())
                .totalPrice(bikeRental.getTotalPrice())
                .dateCreated(bikeRental.getDateCreated())
                .lastDateModified(bikeRental.getLastDateModified())
                .createdBy(bikeRental.getCreatedBy())
                .lastModifiedBy(bikeRental.getLastModifiedBy())
                .build();
    }

    public BikeRental toEntity(BikeRentalDTO dto) {
        if (dto == null) {
            return null;
        }

        BikeRental.BikeRentalBuilder<?, ?> builder = BikeRental.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .startDateTime(dto.getStartDateTime())
                .endDateTime(dto.getEndDateTime())
                .isRevenueSharePaid(dto.getIsRevenueSharePaid())
                .photoUrl(dto.getPhotoUrl())
                .price(dto.getPrice())
                .totalPrice(dto.getTotalPrice());

        if (dto.getBikeId() != null) {
            builder.bike(bikeRepository.findById(dto.getBikeId()).orElse(null));
        }
        if (dto.getLocationId() != null) {
            builder.location(locationRepository.findById(dto.getLocationId()).orElse(null));
        }
        if (dto.getRentalId() != null) {
            builder.rental(rentalRepository.findById(dto.getRentalId()).orElse(null));
        }
        if (dto.getRentalUnitId() != null) {
            builder.rentalUnit(rentalUnitRepository.findById(dto.getRentalUnitId()).orElse(null));
        }
        if (dto.getBikeRentalStatusId() != null) {
            builder.bikeRentalStatus(bikeRentalStatusRepository.findById(dto.getBikeRentalStatusId()).orElse(null));
        }

        return builder.build();
    }
}
