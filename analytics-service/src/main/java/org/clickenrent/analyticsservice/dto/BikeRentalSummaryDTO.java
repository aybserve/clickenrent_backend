package org.clickenrent.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Simplified DTO for bike rental data from rental-service.
 * Contains only the fields needed for analytics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeRentalSummaryDTO {

    private Long id;
    private String externalId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private BigDecimal totalPrice;
    private String bikeRentalStatusName;
    private Integer durationMinutes;
}
