package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for PayoutFinTransaction entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutFinTransactionDTO {
    
    private Long id;
    
    private String externalId;
    
    @NotNull(message = "B2B revenue share payout is required")
    private B2BRevenueSharePayoutDTO b2bRevenueSharePayout;
    
    @NotNull(message = "Financial transaction is required")
    private FinancialTransactionDTO financialTransaction;
    
    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}








