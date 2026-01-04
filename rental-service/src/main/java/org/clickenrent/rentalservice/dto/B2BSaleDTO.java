package org.clickenrent.rentalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for B2BSale entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSaleDTO {

    private Long id;
    private String externalId;
    private Long locationId;
    private Long b2bSaleStatusId;
    private LocalDateTime dateTime;
    
    // Cross-service externalId references
    private String sellerCompanyExternalId;
    private String buyerCompanyExternalId;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
