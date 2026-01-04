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
 * @version 2.0.0
 * 
 * BREAKING CHANGES in v2.0.0:
 * - Removed bikeExternalId (use bikeId to fetch from rental-service)
 * - Removed locationExternalId (use locationId to fetch from rental-service)
 * - Removed rentalExternalId (use rentalId to fetch from rental-service)
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
    private Long rentalUnitId;
    private Long bikeRentalStatusId;
    private Boolean isRevenueSharePaid;
    private String photoUrl;
    private BigDecimal price;
    private BigDecimal totalPrice;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}






