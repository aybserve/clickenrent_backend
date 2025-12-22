package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeReservationDTO;
import org.clickenrent.rentalservice.entity.BikeReservation;
import org.clickenrent.rentalservice.repository.BikeRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between BikeReservation entity and BikeReservationDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeReservationMapper {

    private final BikeRepository bikeRepository;

    public BikeReservationDTO toDto(BikeReservation bikeReservation) {
        if (bikeReservation == null) {
            return null;
        }

        return BikeReservationDTO.builder()
                .id(bikeReservation.getId())
                .externalId(bikeReservation.getExternalId())
                .startDateTime(bikeReservation.getStartDateTime())
                .endDateTime(bikeReservation.getEndDateTime())
                .userExternalId(bikeReservation.getUserExternalId())
                .bikeId(bikeReservation.getBike() != null ? bikeReservation.getBike().getId() : null)
                .build();
    }

    public BikeReservation toEntity(BikeReservationDTO dto) {
        if (dto == null) {
            return null;
        }

        BikeReservation.BikeReservationBuilder builder = BikeReservation.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .startDateTime(dto.getStartDateTime())
                .endDateTime(dto.getEndDateTime())
                .userExternalId(dto.getUserExternalId());

        if (dto.getBikeId() != null) {
            builder.bike(bikeRepository.findById(dto.getBikeId()).orElse(null));
        }

        return builder.build();
    }
}




