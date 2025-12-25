package org.clickenrent.rentalservice.exception;

/**
 * Exception thrown when photo validation fails.
 */
public class PhotoValidationException extends RuntimeException {

    public PhotoValidationException(String message) {
        super(message);
    }

    public PhotoValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

