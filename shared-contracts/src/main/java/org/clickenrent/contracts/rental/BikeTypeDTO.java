package org.clickenrent.contracts.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Shared contract DTO for BikeType entity.
 * Used for cross-service communication.
 * 
 * Source: rental-service
 * Consumers: support-service
 * 
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeTypeDTO {

    private Long id;
    private String externalId;
    private String name;
}



