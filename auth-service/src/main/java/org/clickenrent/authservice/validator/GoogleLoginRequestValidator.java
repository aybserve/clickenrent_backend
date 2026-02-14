package org.clickenrent.authservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.clickenrent.authservice.dto.GoogleLoginRequest;

/**
 * Validator implementation for GoogleLoginRequest.
 * Validates that the request contains valid data for either mobile or web flow.
 */
public class GoogleLoginRequestValidator implements ConstraintValidator<ValidGoogleLoginRequest, GoogleLoginRequest> {
    
    @Override
    public void initialize(ValidGoogleLoginRequest constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(GoogleLoginRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }
        
        boolean hasIdToken = request.getIdToken() != null && !request.getIdToken().isBlank();
        boolean hasCode = request.getCode() != null && !request.getCode().isBlank();
        boolean hasRedirectUri = request.getRedirectUri() != null && !request.getRedirectUri().isBlank();
        
        // Mobile flow: idToken must be present
        boolean validMobileFlow = hasIdToken;
        
        // Web flow: both code and redirectUri must be present
        boolean validWebFlow = hasCode && hasRedirectUri;
        
        // At least one flow must be valid
        if (!validMobileFlow && !validWebFlow) {
            context.disableDefaultConstraintViolation();
            
            // Provide specific error message based on what's missing
            if (hasCode && !hasRedirectUri) {
                context.buildConstraintViolationWithTemplate(
                    "Redirect URI is required when authorization code is provided"
                ).addConstraintViolation();
            } else if (!hasCode && hasRedirectUri) {
                context.buildConstraintViolationWithTemplate(
                    "Authorization code is required when redirect URI is provided"
                ).addConstraintViolation();
            } else {
                context.buildConstraintViolationWithTemplate(
                    "Either 'idToken' (mobile flow) or both 'code' and 'redirectUri' (web flow) must be provided"
                ).addConstraintViolation();
            }
            
            return false;
        }
        
        return true;
    }
}
