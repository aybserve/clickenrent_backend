package org.clickenrent.paymentservice.exception;

/**
 * Exception thrown when an invalid phone number is provided
 */
public class InvalidPhoneNumberException extends RuntimeException {
    
    public InvalidPhoneNumberException(String message) {
        super(message);
    }
    
    public InvalidPhoneNumberException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidPhoneNumberException(String phoneNumber, String reason) {
        super(String.format("Invalid phone number '%s': %s", phoneNumber, reason));
    }
}
