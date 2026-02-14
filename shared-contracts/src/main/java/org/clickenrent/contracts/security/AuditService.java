package org.clickenrent.contracts.security;

/**
 * Interface for audit logging service.
 * Implementations should log security events to appropriate destinations
 * (file, database, external monitoring system, etc.)
 */
public interface AuditService {
    
    /**
     * Log a security audit event
     * 
     * @param event The audit event to log
     */
    void logEvent(AuditEvent event);
    
    /**
     * Log a cross-tenant access attempt
     * 
     * @param userExternalId User's external ID
     * @param userEmail User's email
     * @param userCompanyIds User's company IDs
     * @param attemptedCompanyId Company ID that was attempted to be accessed
     * @param resourceType Type of resource (e.g., "Rental", "Payment")
     * @param endpoint HTTP endpoint
     */
    default void logCrossTenantAccess(
            String userExternalId,
            String userEmail,
            String userCompanyIds,
            String attemptedCompanyId,
            String resourceType,
            String endpoint) {
        
        AuditEvent event = AuditEvent.crossTenantAccessAttempt(
            userExternalId, userEmail, userCompanyIds, 
            attemptedCompanyId, resourceType, endpoint
        );
        logEvent(event);
    }
    
    /**
     * Log an unauthorized access attempt
     * 
     * @param userExternalId User's external ID
     * @param userEmail User's email
     * @param userRoles User's roles
     * @param endpoint HTTP endpoint
     * @param requiredRole Required role for access
     */
    default void logUnauthorizedAccess(
            String userExternalId,
            String userEmail,
            String userRoles,
            String endpoint,
            String requiredRole) {
        
        AuditEvent event = AuditEvent.unauthorizedAccess(
            userExternalId, userEmail, userRoles, endpoint, requiredRole
        );
        logEvent(event);
    }
    
    /**
     * Log a successful authentication
     * 
     * @param userExternalId User's external ID
     * @param userEmail User's email
     * @param clientIp Client IP address
     */
    default void logAuthenticationSuccess(
            String userExternalId,
            String userEmail,
            String clientIp) {
        
        AuditEvent event = AuditEvent.authenticationSuccess(
            userExternalId, userEmail, clientIp
        );
        logEvent(event);
    }
}
