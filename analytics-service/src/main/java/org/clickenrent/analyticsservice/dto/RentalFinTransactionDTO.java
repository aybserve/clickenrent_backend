package org.clickenrent.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for rental financial transaction data from payment-service.
 * Links rental transactions to financial transactions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RentalFinTransactionDTO {

    private Long id;
    private String externalId;
    private String rentalExternalId;
    private FinancialTransactionSummaryDTO financialTransaction;
}
