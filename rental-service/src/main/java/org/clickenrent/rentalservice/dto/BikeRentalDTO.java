package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private Long bikeRentalStatusId;
    private Boolean isRevenueSharePaid;
    private String photoUrl;
    private BigDecimal price;
    private BigDecimal totalPrice;
    
    // Additional fields for analytics
    private String bikeRentalStatusName;
    private String bikeTypeName;
    private String locationName;
    private BigDecimal revenueSharePercent;
    private String rentalExternalId;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
