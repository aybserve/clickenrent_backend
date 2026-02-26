package org.clickenrent.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private BigDecimal totalPrice;
    private String bikeRentalStatusName;
    private Integer durationMinutes;
    private String bikeTypeName;
    
    // Additional fields for revenue analytics
    private BigDecimal revenueSharePercent;
    private Long locationId;
    private String locationName;
    private String rentalExternalId;
}
