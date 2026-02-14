package org.clickenrent.paymentservice.exception;

/**
 * Exception thrown when there's an error with MultiSafePay API integration.
 */
public class MultiSafepayIntegrationException extends RuntimeException {
    
    public MultiSafepayIntegrationException(String message) {
        super(message);
    }
    
    public MultiSafepayIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
