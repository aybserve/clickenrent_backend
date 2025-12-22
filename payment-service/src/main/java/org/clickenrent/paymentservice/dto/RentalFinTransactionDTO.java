package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
    
    private String bikeRentalExternalId; // Optional: References specific bike rental within the rental
    
    @NotNull(message = "Financial transaction is required")
    private FinancialTransactionDTO financialTransaction;
}




