package org.clickenrent.paymentservice.dto.mobile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for mobile payment method information
 * Provides mobile-optimized data for payment method selection
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobilePaymentMethodDTO {
    
    /**
     * Payment method code (e.g., IDEAL, CREDITCARD, PAYPAL)
     */
    private String code;
    
    /**
     * Payment method name (e.g., "iDEAL", "Credit Card")
     */
    private String name;
    
    /**
     * Localized display name for mobile UI
     */
    private String displayName;
    
    /**
     * Icon URL for mobile UI
     */
    private String iconUrl;
    
    /**
     * Flow type: "direct", "redirect", "direct_bank"
     */
    private String flowType;
    
    /**
     * Whether this method requires bank selection (e.g., iDEAL)
     */
    private Boolean requiresBankSelection;
    
    /**
     * Whether this method requires card details
     */
    private Boolean requiresCardDetails;
    
    /**
     * Flag for popular/commonly used methods
     */
    private Boolean popular;
    
    /**
     * Display order for sorting in mobile UI (lower = higher priority)
     */
    private Integer displayOrder;
    
    /**
     * Additional description or instructions
     */
    private String description;
}
