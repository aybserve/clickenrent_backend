package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for PartBrand entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartBrandDTO {

    private Long id;
    private String externalId;
    private String name;
    private Long companyId;

    // Cross-service externalId reference
    private String companyExternalId;
}


