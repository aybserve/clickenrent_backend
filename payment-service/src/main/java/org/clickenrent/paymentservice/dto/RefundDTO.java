package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Refund entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundDTO {
    
    private Long id;
    
    private String externalId;
    
    @NotNull(message = "Financial transaction ID is required")
    private Long financialTransactionId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Currency is required")
    private CurrencyDTO currency;
    
    @NotNull(message = "Refund status is required")
    private RefundStatusDTO refundStatus;
    
    private RefundReasonDTO refundReason;
    
    private String description;
    
    private String initiatedByExternalId;
    
    private LocalDateTime processedAt;
    
    private String stripeRefundId;
    
    private String multisafepayRefundId;
    
    private String companyExternalId;
    
    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}
