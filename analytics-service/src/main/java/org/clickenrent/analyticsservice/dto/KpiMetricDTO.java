package org.clickenrent.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for integer-based KPI metrics.
 * Used for metrics like total bike rentals, active customers, new registrations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KpiMetricDTO {

    private Integer value;
    private Double change;
    private String changeDirection;
    private Integer previousValue;
}
