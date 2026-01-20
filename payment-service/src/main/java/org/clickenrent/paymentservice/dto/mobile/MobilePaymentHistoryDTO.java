package org.clickenrent.paymentservice.dto.mobile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for mobile payment history
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobilePaymentHistoryDTO {
    
    private String transactionExternalId;
    
    private String orderId;
    
    private BigDecimal amount;
    
    private String currency;
    
    private String paymentMethod;
    
    private String status;
    
    private LocalDateTime createdAt;
    
    private String description;
    
    private Boolean isRefund;
}
