package org.clickenrent.paymentservice.exception;

/**
 * Exception thrown when an invalid payment method is used.
 */
public class InvalidPaymentMethodException extends RuntimeException {
    
    public InvalidPaymentMethodException(String message) {
        super(message);
    }
    
    public InvalidPaymentMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}






