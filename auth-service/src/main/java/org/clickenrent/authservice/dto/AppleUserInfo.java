package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing user information extracted from Apple ID token.
 * Apple provides user data in the ID token payload, not a separate userinfo endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppleUserInfo {
    
    /**
     * Apple's unique user identifier (from 'sub' claim)
     */
    private String sub;
    
    /**
     * User's email address
     */
    private String email;
    
    /**
     * Whether the email has been verified by Apple
     */
    private Boolean emailVerified;
    
    /**
     * Whether this is an Apple private relay email
     * Private relay emails end with @privaterelay.appleid.com
     */
    private Boolean isPrivateEmail;
    
    /**
     * User's first name (only provided on first sign-in)
     */
    private String firstName;
    
    /**
     * User's last name (only provided on first sign-in)
     */
    private String lastName;
}
