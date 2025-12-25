package org.clickenrent.contracts.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shared contract DTO for Ride entity.
 * Used for cross-service communication.
 * 
 * Source: rental-service
 * Consumers: auth-service
 * 
 * @version 1.0.0
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
    private Long coordinatesId;
    private Long rideStatusId;
}

