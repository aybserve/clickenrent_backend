package org.clickenrent.gateway;

import org.clickenrent.gateway.ratelimit.CustomRedisRateLimiter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for rate limiting functionality.
 * Uses mocked Redis (TestRedisConfig) so no real Redis server is required.
 */
@SpringBootTest
@ActiveProfiles("test")
class RateLimitIntegrationTest {

    @Autowired(required = false)
    @Qualifier("ipRateLimiter")
    private CustomRedisRateLimiter ipRateLimiter;

    @Autowired(required = false)
    @Qualifier("userRateLimiter")
    private CustomRedisRateLimiter userRateLimiter;

    @Test
    void testIpRateLimiterConfigured() {
        assertNotNull(ipRateLimiter, "IP rate limiter should be configured");
    }

    @Test
    void testUserRateLimiterConfigured() {
        assertNotNull(userRateLimiter, "User rate limiter should be configured");
    }

    @Test
    void testRateLimitersHaveDifferentConfigurations() {
        assertNotNull(ipRateLimiter);
        assertNotNull(userRateLimiter);
        assertNotEquals(ipRateLimiter, userRateLimiter,
                "IP and User rate limiters should be different instances");
    }
}
