package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for BikeReservation entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeReservationDTO {

    private Long id;
    private String externalId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Long userId;
    private Long bikeId;
}


