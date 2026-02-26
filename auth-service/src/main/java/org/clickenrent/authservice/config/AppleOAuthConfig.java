package org.clickenrent.authservice.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

/**
 * Configuration for Apple OAuth authentication.
 * Provides beans for Apple ID token verification and client secret generation.
 */
@Configuration
@EnableCaching
@Slf4j
public class AppleOAuthConfig {
    
    @Value("${oauth2.apple.team-id}")
    private String teamId;
    
    @Value("${oauth2.apple.client-id}")
    private String clientId;
    
    @Value("${oauth2.apple.key-id}")
    private String keyId;
    
    @Value("${oauth2.apple.private-key}")
    private String privateKeyString;
    
    @Value("${oauth2.apple.jwks-uri}")
    private String jwksUri;
    
    /**
     * Parse Apple's P8 private key from string format.
     * The private key is in PKCS8 PEM format.
     * Returns null if the key is not configured (using default placeholder).
     */
    @Bean
    public ECPrivateKey applePrivateKey() {
        // Check if this is a placeholder/default value or missing - skip initialization
        if (privateKeyString == null ||
            privateKeyString.isBlank() ||
            privateKeyString.contains("YOUR_PRIVATE_KEY_CONTENT") ||
            privateKeyString.equals("-----BEGIN PRIVATE KEY-----\\nYOUR_PRIVATE_KEY_CONTENT\\n-----END PRIVATE KEY-----")) {
            log.warn("Apple private key not configured - Apple OAuth will be disabled. Provide a valid key to enable Apple Sign In.");
            return null;
        }
        
        try {
            // Remove any whitespace or newline characters that might have been escaped
            String cleanedKey = privateKeyString
                    .replace("\\n", "\n")
                    .replace("\\\\n", "\n")
                    .trim();
            
            // If key doesn't look like a valid PEM, return null gracefully
            if (!cleanedKey.contains("-----BEGIN") || !cleanedKey.contains("-----END")) {
                log.warn("Apple private key does not appear to be in valid PEM format - Apple OAuth will be disabled.");
                return null;
            }
            
            // Parse PEM format
            PemReader pemReader = new PemReader(new StringReader(cleanedKey));
            PemObject pemObject = pemReader.readPemObject();
            pemReader.close();
            
            if (pemObject == null) {
                log.warn("Failed to parse Apple PEM object - Apple OAuth will be disabled.");
                return null;
            }
            
            byte[] keyBytes = pemObject.getContent();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            
            log.info("Apple private key loaded successfully");
            return (ECPrivateKey) privateKey;
            
        } catch (IOException e) {
            log.warn("Failed to read Apple private key - Apple OAuth will be disabled: {}", e.getMessage());
            return null;
        } catch (NoSuchAlgorithmException e) {
            log.warn("EC algorithm not available - Apple OAuth will be disabled: {}", e.getMessage());
            return null;
        } catch (InvalidKeySpecException e) {
            log.warn("Invalid Apple key specification - Apple OAuth will be disabled: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Unexpected error loading Apple private key - Apple OAuth will be disabled: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Create JWSSigner for signing client_secret JWT.
     * Uses ES256 algorithm (ECDSA with P-256 and SHA-256).
     * Returns null if private key is not configured.
     */
    @Bean
    public JWSSigner appleJwtSigner(ObjectProvider<ECPrivateKey> applePrivateKeyProvider) {
        ECPrivateKey applePrivateKey = applePrivateKeyProvider.getIfAvailable();
        
        if (applePrivateKey == null) {
            log.warn("Apple JWT signer not initialized - private key not configured");
            return null;
        }
        
        try {
            JWSSigner signer = new ECDSASigner(applePrivateKey);
            log.info("Apple JWT signer initialized successfully");
            return signer;
        } catch (JOSEException e) {
            log.error("Failed to create Apple JWT signer", e);
            throw new RuntimeException("Failed to initialize Apple OAuth configuration: cannot create JWT signer", e);
        }
    }
    
    /**
     * Generate client_secret JWT for Apple OAuth.
     * Apple requires a JWT signed with your private key instead of a static secret.
     * The JWT is valid for up to 6 months.
     * 
     * This method is cacheable to avoid regenerating the secret on every request.
     * Cache expires after 5 months to ensure the secret is refreshed before expiration.
     */
    @Cacheable(value = "appleClientSecret", unless = "#result == null")
    public String generateClientSecret(JWSSigner signer) {
        if (signer == null) {
            throw new RuntimeException("Apple JWT signer not configured - cannot generate client_secret");
        }
        
        try {
            Instant now = Instant.now();
            Instant expiration = now.plusSeconds(15552000); // 6 months in seconds
            
            // Create JWT claims
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(teamId)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiration))
                    .audience("https://appleid.apple.com")
                    .subject(clientId)
                    .build();
            
            // Create JWT header with key ID
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                    .keyID(keyId)
                    .build();
            
            // Sign the JWT
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            signedJWT.sign(signer);
            
            String clientSecret = signedJWT.serialize();
            log.debug("Generated Apple client_secret JWT (expires: {})", expiration);
            
            return clientSecret;
            
        } catch (JOSEException e) {
            log.error("Failed to sign Apple client_secret JWT", e);
            throw new RuntimeException("Failed to generate Apple client_secret", e);
        }
    }
    
    /**
     * Create ConfigurableJWTProcessor for verifying Apple ID tokens.
     * Fetches Apple's public keys from JWKS endpoint and verifies token signatures.
     * Returns null if JWKS cannot be fetched (allows startup without Apple configuration).
     */
    @Bean
    public ConfigurableJWTProcessor<SecurityContext> appleJwtProcessor(RestTemplate restTemplate) {
        try {
            // Fetch Apple's public keys (JWKS)
            String jwksJson = restTemplate.getForObject(jwksUri, String.class);
            
            if (jwksJson == null || jwksJson.isEmpty()) {
                log.warn("Apple JWT processor not initialized - JWKS endpoint returned empty response");
                return null;
            }
            
            JWKSet jwkSet = JWKSet.parse(jwksJson);
            
            // Create JWK source with Apple's public keys
            JWKSource<SecurityContext> keySource = new ImmutableJWKSet<>(jwkSet);
            
            // Create JWT processor with ES256 key selector
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
                    JWSAlgorithm.ES256,
                    keySource
            );
            jwtProcessor.setJWSKeySelector(keySelector);
            
            log.info("Apple JWT processor initialized successfully with public keys from JWKS");
            return jwtProcessor;
            
        } catch (ParseException e) {
            log.warn("Failed to parse Apple JWKS - Apple OAuth will not be available: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Failed to fetch Apple JWKS - Apple OAuth will not be available: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Verify and parse Apple ID token.
     * Validates signature, issuer, audience, and expiration.
     * 
     * @param idToken The ID token string
     * @param jwtProcessor The JWT processor for verification
     * @return Parsed and verified JWT claims
     */
    public JWTClaimsSet verifyIdToken(String idToken, ConfigurableJWTProcessor<SecurityContext> jwtProcessor) {
        if (jwtProcessor == null) {
            throw new RuntimeException("Apple JWT processor is not configured - cannot verify ID token");
        }
        
        try {
            // Process and verify the token
            JWTClaimsSet claimsSet = jwtProcessor.process(idToken, null);
            
            // Additional validation
            String issuer = claimsSet.getIssuer();
            if (!"https://appleid.apple.com".equals(issuer)) {
                throw new RuntimeException("Invalid ID token issuer: " + issuer);
            }
            
            String audience = claimsSet.getAudience().get(0);
            if (!clientId.equals(audience)) {
                throw new RuntimeException("ID token audience mismatch: " + audience);
            }
            
            Date expiration = claimsSet.getExpirationTime();
            if (expiration.before(new Date())) {
                throw new RuntimeException("ID token has expired");
            }
            
            log.debug("ID token verified successfully for subject: {}", claimsSet.getSubject());
            return claimsSet;
            
        } catch (ParseException e) {
            log.error("Failed to parse ID token", e);
            throw new RuntimeException("Invalid ID token format", e);
        } catch (BadJOSEException e) {
            log.error("ID token verification failed", e);
            throw new RuntimeException("ID token verification failed: " + e.getMessage(), e);
        } catch (JOSEException e) {
            log.error("JOSE error during ID token verification", e);
            throw new RuntimeException("ID token verification error", e);
        }
    }
}
