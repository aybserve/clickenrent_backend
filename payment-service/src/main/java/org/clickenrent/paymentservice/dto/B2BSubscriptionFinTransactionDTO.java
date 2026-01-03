package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for B2BSubscriptionFinTransaction entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BSubscriptionFinTransactionDTO {
    
    private Long id;
    
    private String externalId;
    
    @NotNull(message = "B2B subscription external ID is required")
    private String b2bSubscriptionExternalId;
    
    @NotNull(message = "Financial transaction is required")
    private FinancialTransactionDTO financialTransaction;
    
    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}




