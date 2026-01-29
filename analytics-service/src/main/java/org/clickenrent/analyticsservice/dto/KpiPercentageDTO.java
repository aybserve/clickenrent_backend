package org.clickenrent.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for percentage-based KPI metrics.
 * Used for metrics like fleet utilization.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KpiPercentageDTO {

    private Double value;
    private String unit;
    private Double change;
    private String changeDirection;
}
