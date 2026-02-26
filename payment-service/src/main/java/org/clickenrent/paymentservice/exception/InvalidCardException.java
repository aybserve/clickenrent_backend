package org.clickenrent.paymentservice.exception;

/**
 * Exception thrown when invalid card details are provided
 */
public class InvalidCardException extends RuntimeException {
    
    public InvalidCardException(String message) {
        super(message);
    }
    
    public InvalidCardException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidCardException(String field, String reason) {
        super(String.format("Invalid card %s: %s", field, reason));
    }
}
