package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for BikeEngine entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeEngineDTO {

    private Long id;
    private String externalId;
    private String name;
}








