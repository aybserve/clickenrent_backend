package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing location summary statistics.
 * Contains counts of total, active, and inactive locations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationSummaryDTO {

    private Integer totalLocations;
    private Integer activeLocations;
    private Integer inactiveLocations;
}
