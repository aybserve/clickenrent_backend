package org.clickenrent.paymentservice.dto.mobile;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.clickenrent.paymentservice.dto.SplitPaymentDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Buy Now Pay Later (BNPL) payment requests
 * Used for Klarna, Billink, in3, Riverty
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BNPLPaymentRequestDTO {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "5.00", message = "BNPL minimum amount is €5.00")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    private String currency;
    
    @NotBlank(message = "Payment method code is required")
    @Pattern(regexp = "KLARNA|BILLINK|IN3|AFTERPAY|PAYAFTER|EINVOICE", 
        message = "Invalid BNPL payment method")
    private String paymentMethodCode;
    
    @NotBlank(message = "Birthday is required for BNPL")
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$", message = "Birthday must be in format YYYY-MM-DD")
    private String birthday;
    
    /**
     * Gender (required for some BNPL methods)
     */
    @Pattern(regexp = "male|female|other", message = "Gender must be male, female, or other")
    private String gender;
    
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Invalid phone number format")
    private String phone;
    
    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required for BNPL")
    private String email;
    
    /**
     * Company type for Billink (private or business)
     */
    @Pattern(regexp = "private|business", message = "Company type must be private or business")
    private String companyType;
    
    @NotBlank(message = "Description is required")
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
    
    /**
     * External rental ID for tracking
     */
    private String rentalExternalId;
    
    /**
     * Split payment configuration (optional)
     */
    private List<SplitPaymentDTO> splits;
    
    /**
     * Shopping cart items (required for BNPL methods)
     */
    private List<ShoppingCartItemDTO> items;
}
