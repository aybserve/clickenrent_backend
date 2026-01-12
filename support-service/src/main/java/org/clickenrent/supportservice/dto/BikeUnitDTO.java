package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for BikeUnit entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeUnitDTO {

    private Long id;
    private String externalId;
    private String name;
    private String companyExternalId;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
