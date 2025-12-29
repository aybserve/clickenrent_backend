package org.clickenrent.authservice.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Configuration for Google OAuth authentication.
 * Provides beans for Google ID token verification.
 */
@Configuration
public class GoogleOAuthConfig {
    
    @Value("${oauth2.google.client-id}")
    private String clientId;
    
    /**
     * Create GoogleIdTokenVerifier bean for verifying ID tokens from Google.
     * This verifier checks:
     * - Token signature (using Google's public keys)
     * - Token audience (must match our client ID)
     * - Token issuer (must be accounts.google.com)
     * - Token expiration
     */
    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }
}

