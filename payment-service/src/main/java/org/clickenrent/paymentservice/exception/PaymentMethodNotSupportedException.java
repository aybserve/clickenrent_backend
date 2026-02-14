package org.clickenrent.paymentservice.exception;

/**
 * Exception thrown when a payment method is not supported or disabled
 */
public class PaymentMethodNotSupportedException extends RuntimeException {
    
    private final String paymentMethod;
    private final String reason;
    
    public PaymentMethodNotSupportedException(String paymentMethod) {
        super(String.format("Payment method '%s' is not supported", paymentMethod));
        this.paymentMethod = paymentMethod;
        this.reason = "not supported";
    }
    
    public PaymentMethodNotSupportedException(String paymentMethod, String reason) {
        super(String.format("Payment method '%s' is not supported: %s", paymentMethod, reason));
        this.paymentMethod = paymentMethod;
        this.reason = reason;
    }
    
    public PaymentMethodNotSupportedException(String message, Throwable cause) {
        super(message, cause);
        this.paymentMethod = "unknown";
        this.reason = "error";
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public String getReason() {
        return reason;
    }
}
