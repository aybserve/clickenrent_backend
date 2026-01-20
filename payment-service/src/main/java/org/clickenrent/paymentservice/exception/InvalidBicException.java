package org.clickenrent.paymentservice.exception;

/**
 * Exception thrown when an invalid BIC/SWIFT code is provided
 */
public class InvalidBicException extends RuntimeException {
    
    public InvalidBicException(String message) {
        super(message);
    }
    
    public InvalidBicException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidBicException(String bic, String reason) {
        super(String.format("Invalid BIC/SWIFT code '%s': %s", bic, reason));
    }
}
