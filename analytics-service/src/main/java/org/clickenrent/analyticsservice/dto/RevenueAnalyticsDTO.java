package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for revenue analytics response.
 * Contains period information, revenue summary, and top locations breakdown.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueAnalyticsDTO {

    private PeriodDTO period;
    private RevenueSummaryDTO summary;
    private List<LocationRevenueDTO> topLocations;
}
