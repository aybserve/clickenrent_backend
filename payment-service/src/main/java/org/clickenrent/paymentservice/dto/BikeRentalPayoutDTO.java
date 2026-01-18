package org.clickenrent.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for bike rental payout information
 * Used to transfer data from rental-service to payment-service for payout processing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeRentalPayoutDTO {
    
    private String bikeRentalExternalId;
    private String locationExternalId;
    private String bikeExternalId;
    private BigDecimal totalPrice;
    private BigDecimal revenueSharePercent;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String rentalExternalId;
}
