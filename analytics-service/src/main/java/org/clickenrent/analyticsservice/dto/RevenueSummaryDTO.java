package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing revenue summary statistics.
 * Contains total revenue, earnings, refunds, and currency information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueSummaryDTO {

    private BigDecimal totalRevenue;
    private BigDecimal totalEarnings;
    private BigDecimal totalRefunds;
    private String currency;
}
