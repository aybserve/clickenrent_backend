package org.clickenrent.rentalservice.dto;

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
 * Data Transfer Object for Rental entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalDTO {

    private Long id;
    
    @Pattern(regexp = "^[a-f0-9-]{36}$", message = "Invalid UUID format")
    private String externalId;
    
    @NotNull(message = "Rental status ID is required")
    private Long rentalStatusId;
    
    @Size(max = 100, message = "ERP rental order ID must not exceed 100 characters")
    private String erpRentalOrderId;

    // Cross-service externalId references
    @NotBlank(message = "User external ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9-]{1,100}$", message = "Invalid user external ID format")
    private String userExternalId;
    
    @Size(max = 100, message = "Company external ID must not exceed 100 characters")
    private String companyExternalId;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}




