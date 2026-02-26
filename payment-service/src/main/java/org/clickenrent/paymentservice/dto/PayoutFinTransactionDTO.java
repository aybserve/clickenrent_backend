package org.clickenrent.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    
    @NotNull(message = "B2B revenue share payout is required")
    private B2BRevenueSharePayoutDTO b2bRevenueSharePayout;
    
    @NotNull(message = "Financial transaction is required")
    private FinancialTransactionDTO financialTransaction;
    
    // Audit fields
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dateCreated;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastDateModified;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdBy;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String lastModifiedBy;
}








