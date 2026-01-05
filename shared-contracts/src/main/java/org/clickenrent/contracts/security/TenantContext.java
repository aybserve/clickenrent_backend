package org.clickenrent.contracts.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thread-local storage for current user's tenant (company) context.
 * Used by all microservices to track which companies the current user can access.
 * 
 * This is a critical security component that ensures tenant isolation across requests.
 * The context is set by TenantInterceptor at the start of each request and cleared after completion.
 */
public class TenantContext {
    
    private static final ThreadLocal<List<String>> currentCompanyIds = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> isSuperAdmin = new ThreadLocal<>();
    
    /**
     * Set the companies the current user has access to.
     * For B2B users: their associated company IDs
     * For Admins: empty (access all)
     * For Customers: empty (access own data only)
     * 
     * @param companyIds List of company external IDs the user can access
     */
    public static void setCurrentCompanies(List<String> companyIds) {
        if (companyIds == null) {
            currentCompanyIds.set(new ArrayList<>());
        } else {
            currentCompanyIds.set(new ArrayList<>(companyIds));
        }
    }
    
    /**
     * Get the companies the current user has access to.
     * Returns unmodifiable list to prevent external modification.
     * 
     * @return Unmodifiable list of company external IDs
     */
    public static List<String> getCurrentCompanies() {
        List<String> companies = currentCompanyIds.get();
        return companies != null ? Collections.unmodifiableList(companies) : Collections.emptyList();
    }
    
    /**
     * Set whether current user is a superadmin (bypasses all tenant filters).
     * Superadmins can access data from all companies for support and debugging purposes.
     * 
     * @param isAdmin true if user is superadmin
     */
    public static void setSuperAdmin(boolean isAdmin) {
        isSuperAdmin.set(isAdmin);
    }
    
    /**
     * Check if current user is a superadmin.
     * 
     * @return true if user is superadmin, false otherwise
     */
    public static boolean isSuperAdmin() {
        return Boolean.TRUE.equals(isSuperAdmin.get());
    }
    
    /**
     * Clear the tenant context. MUST be called after request completes.
     * Failure to clear context can lead to memory leaks and cross-request contamination.
     * 
     * This is typically called in the afterCompletion() method of HandlerInterceptor.
     */
    public static void clear() {
        currentCompanyIds.remove();
        isSuperAdmin.remove();
    }
    
    /**
     * Get a readable summary of current context (for logging and debugging).
     * 
     * @return String representation of current tenant context
     */
    public static String getContextSummary() {
        return String.format("TenantContext[admin=%s, companies=%s]", 
            isSuperAdmin(), getCurrentCompanies());
    }
}
