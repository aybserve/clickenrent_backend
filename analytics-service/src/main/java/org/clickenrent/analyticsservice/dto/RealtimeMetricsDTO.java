package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for real-time analytics metrics.
 * Contains current system metrics for live dashboards.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeMetricsDTO {

    private String timestamp;  // ISO 8601 format
    private Integer activeBikeRentals;
    private Integer bikeRentalsLast15Minutes;
    private BigDecimal revenueLast15Minutes;
    private Integer availableBikes;
    private Integer inProgressSupportRequests;
}
