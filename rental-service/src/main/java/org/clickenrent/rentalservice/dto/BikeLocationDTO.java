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
    private String bikeCode;
    private String bikeModelName;
    private Long bikeStatus;
    private String bikeStatusName;
    private Integer batteryLevel;
    private GeoPointDTO coordinates;
    private Double distance;
    private String distanceUnit;
    private String hubExternalId;
    private String hubName;
}

