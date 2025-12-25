package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling security-related operations.
 * Extracts information from JWT tokens and checks user permissions.
 */
@Service
@RequiredArgsConstructor
public class SecurityService {

    /**
     * Get current user ID from JWT token
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            Object userId = jwt.getClaim("userId");
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            } else if (userId instanceof Long) {
                return (Long) userId;
            }
        }
        
        return null;
    }

    /**
     * Get current user's company IDs from JWT token
     */
    public List<Long> getCurrentUserCompanyIds() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return List.of();
        }
        
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            List<?> companyIds = jwt.getClaim("companyIds");
            if (companyIds != null) {
                return companyIds.stream()
                        .map(id -> {
                            if (id instanceof Integer) {
                                return ((Integer) id).longValue();
                            } else if (id instanceof Long) {
                                return (Long) id;
                            }
                            return null;
                        })
                        .filter(id -> id != null)
                        .collect(Collectors.toList());
            }
        }
        
        return List.of();
    }

    /**
     * Get current user's roles
     */
    public List<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            return List.of();
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());
    }

    /**
     * Check if current user is an admin
     */
    public boolean isAdmin() {
        List<String> roles = getCurrentUserRoles();
        return roles.contains("SUPERADMIN") || roles.contains("ADMIN");
    }

    /**
     * Check if current user is B2B
     */
    public boolean isB2B() {
        List<String> roles = getCurrentUserRoles();
        return roles.contains("B2B");
    }

    /**
     * Check if current user is a customer
     */
    public boolean isCustomer() {
        List<String> roles = getCurrentUserRoles();
        return roles.contains("CUSTOMER");
    }

    /**
     * Check if user has access to a specific company
     */
    public boolean hasAccessToCompany(Long companyId) {
        if (isAdmin()) {
            return true;
        }
        
        List<Long> userCompanyIds = getCurrentUserCompanyIds();
        return userCompanyIds.contains(companyId);
    }

    /**
     * Check if user has access to resource owned by userId
     */
    public boolean hasAccessToUser(Long userId) {
        if (isAdmin()) {
            return true;
        }
        
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }
}






