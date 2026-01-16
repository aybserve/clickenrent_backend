package org.clickenrent.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClosingBalanceDTO {
    private LocalDate date;
    private String currency;
    private BigDecimal openingBalance;
    private BigDecimal turnover;
    private BigDecimal costs;
    private BigDecimal closingBalance;
}
