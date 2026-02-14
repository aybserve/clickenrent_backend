package org.clickenrent.paymentservice.dto.mobile;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for shopping cart items (used in BNPL payments)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartItemDTO {
    
    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Item name must not exceed 100 characters")
    private String name;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be positive")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    /**
     * Item description
     */
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
    
    /**
     * Merchant item ID / SKU
     */
    private String merchantItemId;
    
    /**
     * Tax rate (e.g., 21 for 21% VAT)
     */
    @DecimalMin(value = "0.00", message = "Tax rate cannot be negative")
    @DecimalMax(value = "100.00", message = "Tax rate cannot exceed 100%")
    private BigDecimal taxRate;
    
    /**
     * Product URL
     */
    private String productUrl;
    
    /**
     * Image URL
     */
    private String imageUrl;
}
