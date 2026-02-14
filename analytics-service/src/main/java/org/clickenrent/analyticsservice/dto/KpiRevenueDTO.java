package org.clickenrent.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for revenue-based KPI metrics.
 * Includes currency information and comparison data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KpiRevenueDTO {

    private BigDecimal value;
    private String currency;
    private Double change;
    private String changeDirection;
    private BigDecimal previousValue;
}
