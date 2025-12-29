package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Bike with location information and distance.
 * Used for nearby bike search responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeLocationDTO {

    private String id;
    private String code;
    private String name;
    private Long bikeStatus;
    private String bikeStatusName;
    private Integer batteryLevel;
    private GeoPointDTO location;
    private Double distance;
    private String distanceUnit;
    private String hubExternalId;
    private String hubName;
}

