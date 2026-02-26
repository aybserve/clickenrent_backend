package org.clickenrent.paymentservice.dto.mobile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for bank issuer information (used for iDEAL payments)
 * Provides bank list for mobile UI selection
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileBankDTO {
    
    /**
     * Bank issuer ID (e.g., "0031" for ABN AMRO)
     */
    private String issuerId;
    
    /**
     * Bank name (e.g., "ABN AMRO")
     */
    private String name;
    
    /**
     * Bank icon/logo URL for mobile UI
     */
    private String iconUrl;
}
