package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for fleet analytics response.
 * Contains current fleet status, battery levels, and inspection schedule.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FleetAnalyticsDTO {

    private CurrentStatusDTO currentStatus;
    private BatteryLevelsDTO batteryLevels;
    private BikeInspectionScheduleDTO bikeInspectionSchedule;
}
