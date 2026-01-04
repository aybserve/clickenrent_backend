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

    // Cross-service externalId reference
    private String companyExternalId;

    // Audit fields
    private java.time.LocalDateTime dateCreated;
    private java.time.LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}




