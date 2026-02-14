package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing revenue breakdown by location.
 * Contains location details and associated revenue metrics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationRevenueDTO {

    private Long locationId;
    private String name;
    private BigDecimal revenue;
    private BigDecimal earnings;
    private Double percentage;
}
