package org.clickenrent.authservice.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.authservice.config.RateLimitConfig;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter for rate limiting OAuth endpoints.
 * Applies token bucket algorithm to limit requests per IP address.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {
    
    private final RateLimitConfig rateLimitConfig;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                     HttpServletResponse response, 
                                     FilterChain filterChain) throws ServletException, IOException {
        
        // Skip if rate limiting is disabled
        if (!rateLimitConfig.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Only apply to OAuth endpoints
        String path = request.getRequestURI();
        if (!path.startsWith("/api/auth/google")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Get client IP address
        String ip = getClientIP(request);
        
        // Get bucket for this IP
        Bucket bucket = rateLimitConfig.resolveBucket(ip);
        
        // Try to consume a token
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        if (probe.isConsumed()) {
            // Request allowed - add rate limit headers
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            
            log.warn("Rate limit exceeded for IP: {} on path: {}", ip, path);
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            
            String jsonResponse = String.format(
                    "{\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Please try again in %d seconds.\"}",
                    waitForRefill
            );
            response.getWriter().write(jsonResponse);
        }
    }
    
    /**
     * Extract client IP address from request.
     * Checks X-Forwarded-For header first (for proxied requests).
     * 
     * @param request HTTP request
     * @return Client IP address
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

