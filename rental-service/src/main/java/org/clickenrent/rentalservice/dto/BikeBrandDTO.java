package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for BikeBrand entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeBrandDTO {

    private Long id;
    private String externalId;
    private String name;
    private Long companyId;
}


