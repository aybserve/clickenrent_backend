package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.rentalservice.entity.BikeRental;
import org.clickenrent.rentalservice.entity.Lock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

/**
 * Service for generating encrypted unlock tokens for BLE locks.
 * Uses AES-256 encryption to secure lock communication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LockEncryptionService {

    @Value("${lock.encryption.default-key:change-in-production-32-chars!!}")
    private String defaultEncryptionKey;

    @Value("${lock.token.expiration-seconds:300}")
    private int tokenExpirationSeconds;

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * Generates an encrypted unlock token for a bike rental.
     *
     * @param bikeRental The bike rental
     * @param lock The lock to unlock
     * @return Base64-encoded encrypted token
     */
    public String generateUnlockToken(BikeRental bikeRental, Lock lock) {
        try {
            long currentTimestamp = Instant.now().getEpochSecond();
            long expiresAt = currentTimestamp + tokenExpirationSeconds;

            // Create token payload: rentalId|bikeId|lockId|timestamp|expiresAt
            String payload = String.format("%d|%d|%d|%d|%d",
                    bikeRental.getId(),
                    bikeRental.getBike().getId(),
                    lock.getId(),
                    currentTimestamp,
                    expiresAt);

            // Get encryption key (provider-specific or default)
            String encryptionKey = getEncryptionKey(lock);

            // Encrypt the payload
            byte[] encryptedBytes = encrypt(payload, encryptionKey);

            // Return Base64-encoded token
            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e) {
            log.error("Error generating unlock token for bikeRental {} and lock {}", 
                    bikeRental.getId(), lock.getId(), e);
            throw new RuntimeException("Failed to generate unlock token", e);
        }
    }

    /**
     * Validates and decrypts an unlock token.
     *
     * @param token The encrypted token
     * @param lock The lock
     * @return true if token is valid and not expired
     */
    public boolean validateUnlockToken(String token, Lock lock) {
        try {
            String encryptionKey = getEncryptionKey(lock);
            byte[] decodedBytes = Base64.getDecoder().decode(token);
            String decryptedPayload = decrypt(decodedBytes, encryptionKey);

            String[] parts = decryptedPayload.split("\\|");
            if (parts.length != 5) {
                return false;
            }

            long expiresAt = Long.parseLong(parts[4]);
            long currentTimestamp = Instant.now().getEpochSecond();

            return currentTimestamp < expiresAt;

        } catch (Exception e) {
            log.error("Error validating unlock token for lock {}", lock.getId(), e);
            return false;
        }
    }

    /**
     * Gets the encryption key for a lock (provider-specific or default).
     */
    private String getEncryptionKey(Lock lock) {
        if (lock.getLockProvider() != null && 
            lock.getLockProvider().getEncryptionKey() != null && 
            !lock.getLockProvider().getEncryptionKey().isEmpty()) {
            return lock.getLockProvider().getEncryptionKey();
        }
        return defaultEncryptionKey;
    }

    /**
     * Encrypts data using AES-256.
     */
    private byte[] encrypt(String data, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(padKey(key).getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decrypts data using AES-256.
     */
    private String decrypt(byte[] encryptedData, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(padKey(key).getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedData);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Pads or trims the key to exactly 32 characters for AES-256.
     */
    private String padKey(String key) {
        if (key.length() >= 32) {
            return key.substring(0, 32);
        }
        return String.format("%-32s", key).replace(' ', '0');
    }

    /**
     * Gets the token expiration time in seconds.
     */
    public int getTokenExpirationSeconds() {
        return tokenExpirationSeconds;
    }
}







