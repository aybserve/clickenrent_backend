package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for RentalPlan entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalPlanDTO {

    private Long id;
    private String externalId;
    private String name;
    private Long rentalUnitId;
    private Integer minUnit;
    private Integer maxUnit;
    private Long locationId;
    private BigDecimal defaultPrice;

    // Audit fields
    private java.time.LocalDateTime dateCreated;
    private java.time.LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
