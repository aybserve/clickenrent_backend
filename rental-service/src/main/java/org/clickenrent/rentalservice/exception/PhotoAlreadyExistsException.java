package org.clickenrent.rentalservice.exception;

/**
 * Exception thrown when attempting to upload a photo when one already exists.
 */
public class PhotoAlreadyExistsException extends RuntimeException {

    public PhotoAlreadyExistsException(String message) {
        super(message);
    }

    public PhotoAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}



