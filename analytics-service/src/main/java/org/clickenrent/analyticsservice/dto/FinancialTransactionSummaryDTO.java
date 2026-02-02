package org.clickenrent.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Simplified DTO for financial transaction data from payment-service.
 * Contains only the fields needed for revenue analytics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinancialTransactionSummaryDTO {

    private Long id;
    private String externalId;
    private BigDecimal amount;
    private PaymentStatusDTO paymentStatus;
    
    /**
     * Nested DTO for payment status information.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentStatusDTO {
        private String code;
    }
}
