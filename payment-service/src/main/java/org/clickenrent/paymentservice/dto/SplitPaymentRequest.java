package org.clickenrent.paymentservice.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SplitPaymentRequest {
    @NotBlank(message = "Merchant ID is required")
    @Pattern(regexp = "^[0-9]+$", message = "Merchant ID must be numeric")
    private String merchantId;

    @DecimalMin(value = "0.0", message = "Percentage must be positive or zero")
    @DecimalMax(value = "100.0", message = "Percentage cannot exceed 100")
    private BigDecimal percentage;

    @Min(value = 0, message = "Fixed amount must be positive or zero")
    private Integer fixedAmountCents;

    @Size(max = 255, message = "Description too long")
    private String description;
}
