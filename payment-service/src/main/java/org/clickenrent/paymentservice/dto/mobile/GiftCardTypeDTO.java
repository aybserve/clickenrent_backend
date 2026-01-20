package org.clickenrent.paymentservice.dto.mobile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for gift card type information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftCardTypeDTO {
    
    /**
     * Gift card type code (e.g., "VVVGIFTCARD", "BEAUTYANDWELLNESS")
     */
    private String code;
    
    /**
     * Display name (e.g., "VVV Cadeaukaart", "Beauty & Wellness")
     */
    private String name;
    
    /**
     * Logo URL
     */
    private String logoUrl;
    
    /**
     * Minimum amount for this gift card
     */
    private BigDecimal minAmount;
    
    /**
     * Maximum amount for this gift card
     */
    private BigDecimal maxAmount;
    
    /**
     * Whether PIN is required
     */
    private Boolean requiresPin;
    
    /**
     * Currency supported (usually EUR)
     */
    private String currency;
}
