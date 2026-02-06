package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object containing all dashboard KPI metrics.
 * Aggregates various KPI types into a single response object.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardKPIsDTO {

    private KpiMetricDTO totalBikeRentals;
    private KpiRevenueDTO totalRevenue;
    private KpiMetricDTO activeCustomers;
    private KpiMetricDTO newRegistrations;
    private KpiDurationDTO averageBikeRentalDuration;
}
