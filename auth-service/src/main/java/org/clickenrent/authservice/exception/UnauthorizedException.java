package org.clickenrent.authservice.exception;

/**
 * Exception thrown when authentication fails or user is not authorized.
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
}









