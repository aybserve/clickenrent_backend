package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Ride entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideDTO {

    private Long id;
    private String externalId;
    private Long bikeRentalId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Long startLocationId;
    private Long endLocationId;
    private Long startCoordinatesId;
    private Long endCoordinatesId;
    private Long rideStatusId;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
