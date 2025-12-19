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
 * DTO for FinancialTransaction entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialTransactionDTO {
    
    private Long id;
    
    private String externalId;
    
    @NotNull(message = "Payer ID is required")
    private Long payerId;
    
    @NotNull(message = "Recipient ID is required")
    private Long recipientId;
    
    // Cross-service externalId references
    private String payerExternalId;
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
    
    private Long originalTransactionId;
}


