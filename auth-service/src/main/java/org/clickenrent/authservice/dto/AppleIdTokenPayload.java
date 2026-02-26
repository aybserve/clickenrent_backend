package org.clickenrent.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the full payload of an Apple ID token.
 * Contains all claims from the ID token JWT.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppleIdTokenPayload {
    
    /**
     * Issuer - should be "https://appleid.apple.com"
     */
    @JsonProperty("iss")
    private String issuer;
    
    /**
     * Audience - your client ID
     */
    @JsonProperty("aud")
    private String audience;
    
    /**
     * Expiration time (Unix timestamp)
     */
    @JsonProperty("exp")
    private Long expiration;
    
    /**
     * Issued at time (Unix timestamp)
     */
    @JsonProperty("iat")
    private Long issuedAt;
    
    /**
     * Subject - Apple's unique user identifier
     */
    @JsonProperty("sub")
    private String subject;
    
    /**
     * User's email address
     */
    @JsonProperty("email")
    private String email;
    
    /**
     * Whether the email has been verified
     */
    @JsonProperty("email_verified")
    private Boolean emailVerified;
    
    /**
     * Whether this is a private relay email
     */
    @JsonProperty("is_private_email")
    private Boolean isPrivateEmail;
    
    /**
     * Auth time (Unix timestamp)
     */
    @JsonProperty("auth_time")
    private Long authTime;
    
    /**
     * Nonce used in the request (optional)
     */
    @JsonProperty("nonce")
    private String nonce;
    
    /**
     * Nonce supported flag (optional)
     */
    @JsonProperty("nonce_supported")
    private Boolean nonceSupported;
}
