package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.RideDTO;
import org.clickenrent.rentalservice.entity.Ride;
import org.clickenrent.rentalservice.repository.*;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Ride entity and RideDTO.
 */
@Component
@RequiredArgsConstructor
public class RideMapper {

    private final BikeRentalRepository bikeRentalRepository;
    private final LocationRepository locationRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final RideStatusRepository rideStatusRepository;

    public RideDTO toDto(Ride ride) {
        if (ride == null) {
            return null;
        }

        return RideDTO.builder()
                .id(ride.getId())
                .externalId(ride.getExternalId())
                .bikeRentalId(ride.getBikeRental() != null ? ride.getBikeRental().getId() : null)
                .startDateTime(ride.getStartDateTime())
                .endDateTime(ride.getEndDateTime())
                .startLocationId(ride.getStartLocation() != null ? ride.getStartLocation().getId() : null)
                .endLocationId(ride.getEndLocation() != null ? ride.getEndLocation().getId() : null)
                .startCoordinatesId(ride.getStartCoordinates() != null ? ride.getStartCoordinates().getId() : null)
                .endCoordinatesId(ride.getEndCoordinates() != null ? ride.getEndCoordinates().getId() : null)
                .rideStatusId(ride.getRideStatus() != null ? ride.getRideStatus().getId() : null)
                .dateCreated(ride.getDateCreated())
                .lastDateModified(ride.getLastDateModified())
                .createdBy(ride.getCreatedBy())
                .lastModifiedBy(ride.getLastModifiedBy())
                .build();
    }

    public Ride toEntity(RideDTO dto) {
        if (dto == null) {
            return null;
        }

        Ride.RideBuilder builder = Ride.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .startDateTime(dto.getStartDateTime())
                .endDateTime(dto.getEndDateTime());

        if (dto.getBikeRentalId() != null) {
            builder.bikeRental(bikeRentalRepository.findById(dto.getBikeRentalId()).orElse(null));
        }
        if (dto.getStartLocationId() != null) {
            builder.startLocation(locationRepository.findById(dto.getStartLocationId()).orElse(null));
        }
        if (dto.getEndLocationId() != null) {
            builder.endLocation(locationRepository.findById(dto.getEndLocationId()).orElse(null));
        }
        if (dto.getStartCoordinatesId() != null) {
            builder.startCoordinates(coordinatesRepository.findById(dto.getStartCoordinatesId()).orElse(null));
        }
        if (dto.getEndCoordinatesId() != null) {
            builder.endCoordinates(coordinatesRepository.findById(dto.getEndCoordinatesId()).orElse(null));
        }
        if (dto.getRideStatusId() != null) {
            builder.rideStatus(rideStatusRepository.findById(dto.getRideStatusId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(RideDTO dto, Ride ride) {
        if (dto == null || ride == null) {
            return;
        }

        if (dto.getEndDateTime() != null) {
            ride.setEndDateTime(dto.getEndDateTime());
        }
    }
}
