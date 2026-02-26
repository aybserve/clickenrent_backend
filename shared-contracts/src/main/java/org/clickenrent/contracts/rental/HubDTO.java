package org.clickenrent.contracts.rental;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shared contract DTO for Hub entity.
 * Used for cross-service communication.
 * 
 * Source: rental-service
 * Consumers: search-service
 * 
 * @version 2.4.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubDTO {

    private Long id;
    
    @Pattern(regexp = "^[a-zA-Z0-9-]{1,100}$", message = "Invalid external ID format")
    private String externalId;
    
    @NotBlank(message = "Company external ID is required")
    @Size(max = 100, message = "Company external ID must not exceed 100 characters")
    private String companyExternalId;
    
    @NotBlank(message = "Hub name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;
    
    private Long locationId;
    
    private Integer capacity;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
