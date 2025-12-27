package org.clickenrent.authservice.service;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing blacklisted JWT tokens.
 * In production, consider using Redis or a database for distributed systems.
 */
@Service
public class TokenBlacklistService {

    // In-memory storage for blacklisted tokens
    // Key: token, Value: expiration time
    private final Map<String, Date> blacklistedTokens = new ConcurrentHashMap<>();

    /**
     * Add a token to the blacklist.
     * @param token The JWT token to blacklist
     * @param expirationDate The expiration date of the token
     */
    public void blacklistToken(String token, Date expirationDate) {
        blacklistedTokens.put(token, expirationDate);
        cleanupExpiredTokens();
    }

    /**
     * Check if a token is blacklisted.
     * @param token The JWT token to check
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String token) {
        if (!blacklistedTokens.containsKey(token)) {
            return false;
        }
        
        // Check if the token has expired
        Date expirationDate = blacklistedTokens.get(token);
        if (expirationDate.before(new Date())) {
            // Token has expired, remove it from blacklist
            blacklistedTokens.remove(token);
            return false;
        }
        
        return true;
    }

    /**
     * Remove a token from the blacklist.
     * @param token The JWT token to remove
     */
    public void removeToken(String token) {
        blacklistedTokens.remove(token);
    }

    /**
     * Clean up expired tokens from the blacklist.
     * This method is called periodically to prevent memory leaks.
     */
    private void cleanupExpiredTokens() {
        Date now = new Date();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().before(now));
    }

    /**
     * Get the number of blacklisted tokens (for monitoring purposes).
     * @return The count of currently blacklisted tokens
     */
    public int getBlacklistSize() {
        cleanupExpiredTokens();
        return blacklistedTokens.size();
    }

    /**
     * Clear all blacklisted tokens.
     * Use with caution - typically only for testing purposes.
     */
    public void clearBlacklist() {
        blacklistedTokens.clear();
    }
}









