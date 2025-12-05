package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for BikeRental entity.
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
    private Boolean isRevenueSharePaid;
    private Boolean isB2BRentable;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
