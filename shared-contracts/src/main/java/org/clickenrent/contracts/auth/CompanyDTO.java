package org.clickenrent.contracts.auth;

import jakarta.validation.constraints.*;
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
    
    @Pattern(regexp = "^[a-f0-9-]{36}$", message = "Invalid UUID format")
    private String externalId;
    
    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @Size(max = 200, message = "Website must not exceed 200 characters")
    private String website;
    
    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    private String logo;
    
    @Size(max = 100, message = "ERP Partner ID must not exceed 100 characters")
    private String erpPartnerId;
    
    @NotNull(message = "Company type ID is required")
    private Long companyTypeId;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}






