package org.clickenrent.paymentservice.dto.mobile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for mobile refund status response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileRefundStatusDTO {
    
    private String orderId;
    
    private String refundId;
    
    private BigDecimal amount;
    
    private String currency;
    
    private String status;
    
    private LocalDateTime createdAt;
    
    private String description;
}
