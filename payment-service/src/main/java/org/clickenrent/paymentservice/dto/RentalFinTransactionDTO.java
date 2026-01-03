package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for RentalFinTransaction entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalFinTransactionDTO {
    
    private Long id;
    
    private String externalId;
    
    // Cross-service externalId references
    @NotNull(message = "Rental external ID is required")
    private String rentalExternalId;
    
    @NotNull(message = "Financial transaction is required")
    private FinancialTransactionDTO financialTransaction;
    
    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}




