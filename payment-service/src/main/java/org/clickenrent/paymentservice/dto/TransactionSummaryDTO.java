package org.clickenrent.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSummaryDTO {
    private String transactionId;
    private String orderId;
    private LocalDateTime created;
    private LocalDateTime modified;
    private String status;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String paymentMethod;
    private String financialStatus;
    private String reason;
    private String reasonCode;
}
