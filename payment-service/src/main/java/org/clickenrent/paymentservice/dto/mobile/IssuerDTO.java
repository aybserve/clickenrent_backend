package org.clickenrent.paymentservice.dto.mobile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for bank issuer information (used for iDEAL, Bancontact, Dotpay, MyBank)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssuerDTO {
    
    /**
     * Issuer code/ID (e.g., "3151" for ABN AMRO in iDEAL)
     */
    private String code;
    
    /**
     * Issuer display name (e.g., "ABN AMRO", "ING Bank")
     */
    private String name;
    
    /**
     * Optional issuer logo URL
     */
    private String logoUrl;
    
    /**
     * Payment method this issuer belongs to (e.g., "IDEAL", "BANCONTACT")
     */
    private String paymentMethod;
    
    /**
     * Whether this issuer is currently available
     */
    private Boolean available;
}
