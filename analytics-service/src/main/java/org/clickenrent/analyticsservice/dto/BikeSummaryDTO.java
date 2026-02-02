package org.clickenrent.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simplified DTO for bike data from rental-service.
 * Contains only the fields needed for fleet analytics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BikeSummaryDTO {

    private Long id;
    private String externalId;
    private Long bikeStatusId;
    private Integer batteryLevel;
}
