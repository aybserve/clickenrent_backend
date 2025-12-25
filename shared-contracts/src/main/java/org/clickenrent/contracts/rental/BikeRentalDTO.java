package org.clickenrent.contracts.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Shared contract DTO for BikeRental entity.
 * Used for cross-service communication.
 * 
 * Source: rental-service
 * Consumers: payment-service
 * 
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeRentalDTO {

    private Long id;
    private String externalId;
    private Long bikeId;
    private Long locationId;
    private Long rentalId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private BigDecimal price;
    private BigDecimal totalPrice;

    // Cross-service externalId references
    private String bikeExternalId;
    private String locationExternalId;
    private String rentalExternalId;
}




