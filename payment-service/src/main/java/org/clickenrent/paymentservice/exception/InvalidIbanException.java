package org.clickenrent.paymentservice.exception;

/**
 * Exception thrown when an invalid IBAN is provided
 */
public class InvalidIbanException extends RuntimeException {
    
    public InvalidIbanException(String message) {
        super(message);
    }
    
    public InvalidIbanException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidIbanException(String iban, String reason) {
        super(String.format("Invalid IBAN '%s': %s", iban, reason));
    }
}
