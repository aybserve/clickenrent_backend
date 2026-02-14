package org.clickenrent.authservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for GoogleLoginRequest.
 * Ensures that either:
 * - idToken is provided (mobile flow), OR
 * - both code AND redirectUri are provided (web flow)
 * 
 * Validation fails if:
 * - Neither flow is provided
 * - Web flow is incomplete (code without redirectUri, or vice versa)
 */
@Documented
@Constraint(validatedBy = GoogleLoginRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGoogleLoginRequest {
    
    String message() default "Either 'idToken' (mobile flow) or both 'code' and 'redirectUri' (web flow) must be provided";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
