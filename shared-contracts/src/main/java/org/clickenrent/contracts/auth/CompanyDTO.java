package org.clickenrent.contracts.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shared contract DTO for Company entity.
 * Used for cross-service communication.
 * 
 * Source: auth-service
 * Consumers: rental-service, payment-service, support-service
 * 
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {

    private Long id;
    private String externalId;
    private String name;
    private String description;
    private String website;
    private String logo;
    private String erpPartnerId;
    private Long companyTypeId;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}





