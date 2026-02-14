package org.clickenrent.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for FinancialTransaction entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialTransactionDTO {
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String externalId;
    
    // Cross-service externalId references
    @NotNull(message = "Payer external ID is required")
    private String payerExternalId;
    
    @NotNull(message = "Recipient external ID is required")
    private String recipientExternalId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Currency is required")
    private CurrencyDTO currency;
    
    private LocalDateTime dateTime;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethodDTO paymentMethod;
    
    @NotNull(message = "Payment status is required")
    private PaymentStatusDTO paymentStatus;
    
    private ServiceProviderDTO serviceProvider;
    
    private String stripePaymentIntentId;
    
    private String stripeChargeId;
    
    private String stripeRefundId;
    
    private String multiSafepayOrderId;
    
    private String multiSafepayTransactionId;
    
    private Long originalTransactionId;
    
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




