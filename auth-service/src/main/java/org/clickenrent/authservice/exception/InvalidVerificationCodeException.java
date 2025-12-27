package org.clickenrent.authservice.exception;

import lombok.Getter;

/**
 * Exception thrown when email verification code is invalid, expired, or max attempts reached.
 */
@Getter
public class InvalidVerificationCodeException extends RuntimeException {

    private final Integer attemptsRemaining;

    public InvalidVerificationCodeException(String message, Integer attemptsRemaining) {
        super(message);
        this.attemptsRemaining = attemptsRemaining;
    }

    public InvalidVerificationCodeException(String message) {
        super(message);
        this.attemptsRemaining = 0;
    }
}







