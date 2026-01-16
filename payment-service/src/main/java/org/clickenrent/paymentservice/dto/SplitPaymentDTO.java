package org.clickenrent.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SplitPaymentDTO {
    private String merchantId;
    private BigDecimal percentage;
    private Integer fixedAmountCents;
    private String description;
}
