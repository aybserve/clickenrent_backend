package org.clickenrent.paymentservice.exception;

/**
 * Exception thrown when trying to use a disabled payment method
 */
public class PaymentMethodDisabledException extends RuntimeException {
    
    private final String paymentMethod;
    
    public PaymentMethodDisabledException(String paymentMethod) {
        super(String.format("Payment method '%s' is currently disabled", paymentMethod));
        this.paymentMethod = paymentMethod;
    }
    
    public PaymentMethodDisabledException(String paymentMethod, String reason) {
        super(String.format("Payment method '%s' is currently disabled: %s", paymentMethod, reason));
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
}
