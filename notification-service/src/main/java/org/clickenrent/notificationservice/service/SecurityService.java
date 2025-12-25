package org.clickenrent.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Service for security-related operations.
 */
@Service
@Slf4j
public class SecurityService {

    /**
     * Get the current authenticated user's external ID from JWT token.
     *
     * @return User external ID
     */
    public String getCurrentUserExternalId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authenticated user found");
            throw new IllegalStateException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            // Extract user external ID from JWT claims
            Object userExternalIdClaim = jwt.getClaim("userExternalId");
            if (userExternalIdClaim != null) {
                return userExternalIdClaim.toString();
            }

            // Fallback: try to get from subject
            String subject = jwt.getSubject();
            if (subject != null && !subject.isEmpty()) {
                return subject;
            }
        }

        log.error("Could not extract user external ID from authentication");
        throw new IllegalStateException("Could not extract user external ID from token");
    }

    /**
     * Get the current authenticated username.
     *
     * @return Username
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }
}

