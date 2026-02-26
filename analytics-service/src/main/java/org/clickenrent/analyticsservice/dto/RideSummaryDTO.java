package org.clickenrent.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Simplified DTO for ride data from rental-service.
 * Contains only the fields needed for analytics calculations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideSummaryDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    private Long bikeRentalId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Long rideStatusId;
}
