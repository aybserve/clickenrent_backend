package org.clickenrent.authservice.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.clickenrent.authservice.dto.GoogleLoginRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GoogleLoginRequest validation.
 * Tests the custom validator that ensures proper flow validation.
 */
class GoogleLoginRequestValidatorTest {
    
    private static Validator validator;
    
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    @DisplayName("Valid mobile flow - idToken only")
    void testValidMobileFlow() {
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .idToken("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.test.token")
                .build();
        
        Set<ConstraintViolation<GoogleLoginRequest>> violations = validator.validate(request);
        
        assertThat(violations).isEmpty();
    }
    
    @Test
    @DisplayName("Valid web flow - code and redirectUri")
    void testValidWebFlow() {
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .code("4/0AY0e-g7...")
                .redirectUri("http://localhost:3000/auth/google/callback")
                .build();
        
        Set<ConstraintViolation<GoogleLoginRequest>> violations = validator.validate(request);
        
        assertThat(violations).isEmpty();
    }
    
    @Test
    @DisplayName("Valid - both flows provided (mobile takes precedence)")
    void testBothFlowsProvided() {
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .idToken("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.test.token")
                .code("4/0AY0e-g7...")
                .redirectUri("http://localhost:3000/auth/google/callback")
                .build();
        
        Set<ConstraintViolation<GoogleLoginRequest>> violations = validator.validate(request);
        
        // Should be valid - validator allows both, controller decides which to use
        assertThat(violations).isEmpty();
    }
    
    @Test
    @DisplayName("Invalid - neither flow provided")
    void testNeitherFlowProvided() {
        GoogleLoginRequest request = GoogleLoginRequest.builder().build();
        
        Set<ConstraintViolation<GoogleLoginRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Either 'idToken' (mobile flow) or both 'code' and 'redirectUri' (web flow) must be provided");
    }
    
    @Test
    @DisplayName("Invalid - code without redirectUri")
    void testCodeWithoutRedirectUri() {
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .code("4/0AY0e-g7...")
                .build();
        
        Set<ConstraintViolation<GoogleLoginRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Redirect URI is required when authorization code is provided");
    }
    
    @Test
    @DisplayName("Invalid - redirectUri without code")
    void testRedirectUriWithoutCode() {
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .redirectUri("http://localhost:3000/auth/google/callback")
                .build();
        
        Set<ConstraintViolation<GoogleLoginRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Authorization code is required when redirect URI is provided");
    }
    
    @Test
    @DisplayName("Invalid - empty idToken")
    void testEmptyIdToken() {
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .idToken("")
                .build();
        
        Set<ConstraintViolation<GoogleLoginRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
    }
    
    @Test
    @DisplayName("Invalid - blank idToken")
    void testBlankIdToken() {
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .idToken("   ")
                .build();
        
        Set<ConstraintViolation<GoogleLoginRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
    }
    
    @Test
    @DisplayName("Invalid - empty code in web flow")
    void testEmptyCodeInWebFlow() {
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .code("")
                .redirectUri("http://localhost:3000/auth/google/callback")
                .build();
        
        Set<ConstraintViolation<GoogleLoginRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
    }
    
    @Test
    @DisplayName("Invalid - empty redirectUri in web flow")
    void testEmptyRedirectUriInWebFlow() {
        GoogleLoginRequest request = GoogleLoginRequest.builder()
                .code("4/0AY0e-g7...")
                .redirectUri("")
                .build();
        
        Set<ConstraintViolation<GoogleLoginRequest>> violations = validator.validate(request);
        
        assertThat(violations).hasSize(1);
    }
}
