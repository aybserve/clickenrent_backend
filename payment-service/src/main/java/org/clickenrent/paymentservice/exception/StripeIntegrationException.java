package org.clickenrent.paymentservice.exception;

/**
 * Exception thrown when there's an error with Stripe API integration.
 */
public class StripeIntegrationException extends RuntimeException {
    
    public StripeIntegrationException(String message) {
        super(message);
    }
    
    public StripeIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}




