package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Hub entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubDTO {

    private Long id;
    private String externalId;
    private String name;
    private Long locationId;
    private String directions;
    private Long coordinatesId;

    // Audit fields
    private java.time.LocalDateTime dateCreated;
    private java.time.LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}








