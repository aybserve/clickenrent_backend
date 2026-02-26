package org.clickenrent.contracts.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shared contract DTO for BikeType entity.
 * Used for cross-service communication.
 * 
 * Source: rental-service
 * Consumers: support-service
 * 
 * @version 2.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeTypeDTO {

    private Long id;
    private String externalId;
    private String name;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}





