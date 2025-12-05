package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for BikeModel entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeModelDTO {

    private Long id;
    private String externalId;
    private String name;
    private Long bikeBrandId;
    private String imageUrl;
    private Long bikeTypeId;
    private Long bikeEngineId;
}
