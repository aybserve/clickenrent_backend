package org.clickenrent.authservice.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Configuration for Google OAuth authentication.
 * Provides beans for Google ID token verification with public key caching.
 */
@Configuration
@EnableCaching
@Slf4j
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
     * 
     * Uses GoogleNetHttpTransport which:
     * - Automatically caches Google's public keys
     * - Refreshes keys when they rotate
     * - Reduces latency and external API calls
     */
    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() {
        try {
            // GoogleNetHttpTransport automatically handles key caching and rotation
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, new GsonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    // Google public keys are cached and automatically refreshed
                    // Default cache duration is based on cache-control headers from Google
                    .build();
            
            log.info("GoogleIdTokenVerifier initialized with automatic public key caching");
            return verifier;
            
        } catch (GeneralSecurityException | IOException e) {
            log.error("Failed to create trusted HTTP transport for Google OAuth", e);
            throw new RuntimeException("Failed to initialize Google OAuth configuration", e);
        }
    }
}


