package org.clickenrent.authservice.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.security.TenantContext;
import org.clickenrent.authservice.service.SecurityService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * Extracts tenant (company) context from JWT and stores in TenantContext.
 * Runs before every request to auth-service endpoints.
 * 
 * This interceptor is critical for multi-tenant security:
 * 1. Extracts company IDs from JWT token claims
 * 2. Stores them in ThreadLocal (TenantContext)
 * 3. Makes them available to RLS policies
 * 4. Clears context after request to prevent memory leaks
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {
    
    private final SecurityService securityService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // Determine if user is superadmin
            boolean isAdmin = securityService.isAdmin();
            TenantContext.setSuperAdmin(isAdmin);
            
            // Extract company IDs from JWT
            if (!isAdmin && securityService.isB2B()) {
                List<String> companyIds = securityService.getCurrentUserCompanyExternalIds();
                TenantContext.setCurrentCompanies(companyIds);
                log.debug("Tenant context initialized: B2B user with companies={}", companyIds);
            } else if (!isAdmin) {
                // Customer user - no companies
                TenantContext.setCurrentCompanies(List.of());
                log.debug("Tenant context initialized: Customer user");
            } else {
                // Admin user - access to all
                TenantContext.setCurrentCompanies(List.of());
                log.debug("Tenant context initialized: Admin user (bypass filters)");
            }
            
            return true;
        } catch (Exception e) {
            log.error("Failed to initialize tenant context", e);
            TenantContext.clear();
            return false;
        }
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        // Critical: Clear context to prevent memory leaks and cross-request contamination
        TenantContext.clear();
        log.trace("Tenant context cleared");
    }
}
