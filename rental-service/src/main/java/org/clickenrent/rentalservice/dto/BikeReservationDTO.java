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
    private String userExternalId;
    private Long bikeId;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}




