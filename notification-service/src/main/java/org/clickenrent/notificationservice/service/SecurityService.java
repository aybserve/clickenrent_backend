package org.clickenrent.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service for security-related operations and multi-tenant context.
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

    /**
     * Get the current user's company external IDs from JWT token.
     * Used for multi-tenant isolation.
     *
     * @return List of company external IDs
     */
    @SuppressWarnings("unchecked")
    public List<String> getCurrentUserCompanyExternalIds() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authenticated user found");
            return Collections.emptyList();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            // Extract company external IDs from JWT claims
            Object companyExternalIdsClaim = jwt.getClaim("companyExternalIds");
            if (companyExternalIdsClaim instanceof List) {
                return (List<String>) companyExternalIdsClaim;
            }
        }

        log.debug("No company external IDs found in token");
        return Collections.emptyList();
    }

    /**
     * Check if the current user is an admin.
     *
     * @return true if user has ADMIN role
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ADMIN"));
    }

    /**
     * Check if the current user is a B2B user.
     *
     * @return true if user has B2B role
     */
    public boolean isB2B() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_B2B") || role.equals("B2B"));
    }
}

