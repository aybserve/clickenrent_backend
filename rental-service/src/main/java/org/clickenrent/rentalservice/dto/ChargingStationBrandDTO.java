package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for ChargingStationBrand entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStationBrandDTO {

    private Long id;
    private String externalId;
    private String name;

    // Cross-service externalId reference
    private String companyExternalId;
}




