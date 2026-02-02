package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for location analytics response.
 * Contains location summary statistics and full list of locations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationAnalyticsDTO {

    private LocationSummaryDTO summary;
    private List<LocationDTO> locations;
}
