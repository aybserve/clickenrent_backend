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
    private Long bikeModelId;
    private Long rentalPlanId;
    private BigDecimal price;
}
