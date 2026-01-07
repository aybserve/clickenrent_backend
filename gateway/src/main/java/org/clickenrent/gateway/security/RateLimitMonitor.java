package org.clickenrent.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Monitors rate limit violations and implements attack detection.
 * Tracks repeated violations and can auto-block suspicious IPs.
 */
@Slf4j
@Component
public class RateLimitMonitor {
    
    @Value("${rate-limit.attack.threshold:10}")
    private int attackThreshold;
    
    @Value("${rate-limit.attack.block-duration-minutes:15}")
    private int blockDurationMinutes;
    
    @Value("${rate-limit.attack.detection-window-minutes:5}")
    private int detectionWindowMinutes;
    
    // Track violations per IP: IP -> ViolationTracker
    private final Map<String, ViolationTracker> violations = new ConcurrentHashMap<>();
    
    // Track blocked IPs: IP -> Block expiration timestamp
    private final Map<String, Instant> blockedIps = new ConcurrentHashMap<>();
    
    /**
     * Record a rate limit violation for an IP address.
     * 
     * @param ip Client IP address
     * @param userId User ID (if authenticated)
     * @param path Request path
     */
    public void recordViolation(String ip, String userId, String path) {
        ViolationTracker tracker = violations.computeIfAbsent(ip, k -> new ViolationTracker());
        tracker.recordViolation();
        
        int count = tracker.getViolationCount();
        
        log.warn("Rate limit violation #{} - IP: {}, User: {}, Path: {}", 
                count, ip, userId != null ? userId : "anonymous", path);
        
        // Check if threshold exceeded
        if (count >= attackThreshold) {
            blockIp(ip, userId);
        } else if (count >= attackThreshold / 2) {
            // Warning at 50% of threshold
            log.warn("Rate limit violation warning - IP {} has {} violations (threshold: {})", 
                    ip, count, attackThreshold);
        }
    }
    
    /**
     * Check if an IP is currently blocked.
     * 
     * @param ip Client IP address
     * @return true if blocked, false otherwise
     */
    public boolean isBlocked(String ip) {
        Instant blockExpiry = blockedIps.get(ip);
        
        if (blockExpiry == null) {
            return false;
        }
        
        // Check if block has expired
        if (Instant.now().isAfter(blockExpiry)) {
            blockedIps.remove(ip);
            violations.remove(ip);
            log.info("Block expired for IP: {}", ip);
            return false;
        }
        
        return true;
    }
    
    /**
     * Get remaining block time in seconds.
     * 
     * @param ip Client IP address
     * @return Remaining seconds, or 0 if not blocked
     */
    public long getRemainingBlockTime(String ip) {
        Instant blockExpiry = blockedIps.get(ip);
        
        if (blockExpiry == null) {
            return 0;
        }
        
        long remaining = blockExpiry.getEpochSecond() - Instant.now().getEpochSecond();
        return Math.max(0, remaining);
    }
    
    /**
     * Block an IP address for repeated violations.
     * 
     * @param ip Client IP address
     * @param userId User ID (if authenticated)
     */
    private void blockIp(String ip, String userId) {
        Instant blockExpiry = Instant.now().plusSeconds(blockDurationMinutes * 60L);
        blockedIps.put(ip, blockExpiry);
        
        log.error("SECURITY ALERT: IP {} blocked for {} minutes due to excessive rate limit violations. User: {}", 
                ip, blockDurationMinutes, userId != null ? userId : "anonymous");
        
        // TODO: Send alert to monitoring system (e.g., Slack, email, PagerDuty)
        // alertService.sendSecurityAlert("Rate Limit Attack Detected", ip, userId);
    }
    
    /**
     * Manually unblock an IP address (for administrative purposes).
     * 
     * @param ip Client IP address
     */
    public void unblockIp(String ip) {
        blockedIps.remove(ip);
        violations.remove(ip);
        log.info("IP {} manually unblocked", ip);
    }
    
    /**
     * Get current violation statistics.
     * 
     * @return Map of IP addresses to violation counts
     */
    public Map<String, Integer> getViolationStats() {
        Map<String, Integer> stats = new ConcurrentHashMap<>();
        violations.forEach((ip, tracker) -> stats.put(ip, tracker.getViolationCount()));
        return stats;
    }
    
    /**
     * Clean up expired violations (called periodically).
     */
    public void cleanupExpiredViolations() {
        Instant cutoff = Instant.now().minusSeconds(detectionWindowMinutes * 60L);
        
        violations.entrySet().removeIf(entry -> {
            ViolationTracker tracker = entry.getValue();
            if (tracker.getLastViolation().isBefore(cutoff)) {
                log.debug("Cleaning up expired violations for IP: {}", entry.getKey());
                return true;
            }
            return false;
        });
    }
    
    /**
     * Inner class to track violations for an IP address.
     */
    private static class ViolationTracker {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile Instant lastViolation = Instant.now();
        
        public void recordViolation() {
            count.incrementAndGet();
            lastViolation = Instant.now();
        }
        
        public int getViolationCount() {
            return count.get();
        }
        
        public Instant getLastViolation() {
            return lastViolation;
        }
    }
}
