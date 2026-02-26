package org.clickenrent.paymentservice.exception;

/**
 * Exception thrown when a bank issuer is not available for a payment method
 */
public class IssuerNotAvailableException extends RuntimeException {
    
    private final String issuerId;
    private final String paymentMethod;
    
    public IssuerNotAvailableException(String issuerId, String paymentMethod) {
        super(String.format("Issuer '%s' is not available for payment method '%s'", issuerId, paymentMethod));
        this.issuerId = issuerId;
        this.paymentMethod = paymentMethod;
    }
    
    public IssuerNotAvailableException(String message) {
        super(message);
        this.issuerId = "unknown";
        this.paymentMethod = "unknown";
    }
    
    public IssuerNotAvailableException(String message, Throwable cause) {
        super(message, cause);
        this.issuerId = "unknown";
        this.paymentMethod = "unknown";
    }
    
    public String getIssuerId() {
        return issuerId;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
}
