package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Main DTO for bike rental analytics response.
 * Contains period, summary, duration, peak times, and bike type breakdown.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeRentalAnalyticsDTO {

    private PeriodDTO period;
    private RentalSummaryDTO summary;
    private RentalDurationDTO duration;
    private List<PeakHourDTO> peakHours;
    private List<PeakDayDTO> peakDays;
    private List<BikeTypeBreakdownDTO> bikeTypeBreakdown;
}
