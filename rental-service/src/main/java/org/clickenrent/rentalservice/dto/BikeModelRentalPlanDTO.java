package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for BikeModelRentalPlan entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeModelRentalPlanDTO {

    private Long id;
    private String externalId;
    private Long bikeModelId;
    private Long rentalPlanId;
    private BigDecimal price;

    // Audit fields
    private java.time.LocalDateTime dateCreated;
    private java.time.LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}








