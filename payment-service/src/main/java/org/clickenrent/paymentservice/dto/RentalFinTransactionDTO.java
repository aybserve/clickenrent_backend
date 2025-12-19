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
    
    @NotNull(message = "Rental ID is required")
    private Long rentalId;
    
    private Long bikeRentalId; // Optional: References specific bike rental within the rental
    
    // Cross-service externalId references
    private String rentalExternalId;
    private String bikeRentalExternalId;
    
    @NotNull(message = "Financial transaction is required")
    private FinancialTransactionDTO financialTransaction;
}


