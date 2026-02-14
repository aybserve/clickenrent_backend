package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Data Transfer Object for dashboard overview response.
 * Contains period information, aggregated KPIs, and generation timestamp.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewDTO {

    private PeriodDTO period;
    private DashboardKPIsDTO kpis;
    private ZonedDateTime generatedAt;
}
