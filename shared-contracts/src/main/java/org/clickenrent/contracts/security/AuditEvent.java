package org.clickenrent.contracts.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents a security audit event for logging and monitoring.
 * Used to track security-related activities across all microservices.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {
    
    /**
     * Type of security event
     */
    private EventType eventType;
    
    /**
     * External ID of the user who triggered the event
     */
    private String userExternalId;
    
    /**
     * Email of the user (for easier identification)
     */
    private String userEmail;
    
    /**
     * User's roles at the time of the event
     */
    private String userRoles;
    
    /**
     * Company external IDs the user has access to
     */
    private String userCompanyIds;
    
    /**
     * Company external ID that was attempted to be accessed
     */
    private String attemptedCompanyId;
    
    /**
     * Resource that was accessed (e.g., "Rental", "Payment", "SupportRequest")
     */
    private String resourceType;
    
    /**
     * External ID of the specific resource accessed
     */
    private String resourceId;
    
    /**
     * HTTP endpoint that was called
     */
    private String endpoint;
    
    /**
     * HTTP method (GET, POST, PUT, DELETE)
     */
    private String httpMethod;
    
    /**
     * Service name where the event occurred
     */
    private String serviceName;
    
    /**
     * Detailed message about the event
     */
    private String message;
    
    /**
     * Additional context data (stored as JSON)
     */
    private Map<String, Object> metadata;
    
    /**
     * Timestamp when the event occurred
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    /**
     * IP address of the client
     */
    private String clientIp;
    
    /**
     * User agent string
     */
    private String userAgent;
    
    /**
     * Whether the action was allowed or denied
     */
    private boolean allowed;
    
    /**
     * Whether the action was successful
     */
    private boolean success;
    
    /**
     * Error message if action failed
     */
    private String errorMessage;
    
    /**
     * Types of security events that can be audited
     */
    public enum EventType {
        /**
         * User attempted to access data from a company they don't belong to
         */
        CROSS_TENANT_ACCESS_ATTEMPT,
        
        /**
         * User attempted an action they don't have permission for
         */
        UNAUTHORIZED_ACCESS,
        
        /**
         * Successful authentication
         */
        AUTHENTICATION_SUCCESS,
        
        /**
         * Failed authentication attempt
         */
        AUTHENTICATION_FAILURE,
        
        /**
         * User logged out
         */
        LOGOUT,
        
        /**
         * JWT token expired or invalid
         */
        TOKEN_VALIDATION_FAILURE,
        
        /**
         * Tenant context was not properly initialized
         */
        TENANT_CONTEXT_ERROR,
        
        /**
         * Hibernate filter detected cross-tenant data
         */
        FILTER_VIOLATION,
        
        /**
         * PostgreSQL RLS blocked a query
         */
        RLS_POLICY_VIOLATION,
        
        /**
         * Runtime validation caught a cross-tenant leak
         */
        RUNTIME_VALIDATION_FAILURE,
        
        /**
         * Suspicious activity detected (e.g., SQL injection attempt)
         */
        SUSPICIOUS_ACTIVITY,
        
        /**
         * Admin performed a privileged action
         */
        ADMIN_ACTION,
        
        /**
         * User's company associations changed
         */
        COMPANY_ASSOCIATION_CHANGED,
        
        /**
         * Security configuration changed
         */
        SECURITY_CONFIG_CHANGED
    }
    
    /**
     * Create an audit event for cross-tenant access attempt
     */
    public static AuditEvent crossTenantAccessAttempt(
            String userExternalId,
            String userEmail,
            String userCompanyIds,
            String attemptedCompanyId,
            String resourceType,
            String endpoint) {
        
        return AuditEvent.builder()
                .eventType(EventType.CROSS_TENANT_ACCESS_ATTEMPT)
                .userExternalId(userExternalId)
                .userEmail(userEmail)
                .userCompanyIds(userCompanyIds)
                .attemptedCompanyId(attemptedCompanyId)
                .resourceType(resourceType)
                .endpoint(endpoint)
                .allowed(false)
                .message(String.format(
                    "User %s (companies: %s) attempted to access %s from company %s",
                    userEmail, userCompanyIds, resourceType, attemptedCompanyId
                ))
                .build();
    }
    
    /**
     * Create an audit event for unauthorized access
     */
    public static AuditEvent unauthorizedAccess(
            String userExternalId,
            String userEmail,
            String userRoles,
            String endpoint,
            String requiredRole) {
        
        return AuditEvent.builder()
                .eventType(EventType.UNAUTHORIZED_ACCESS)
                .userExternalId(userExternalId)
                .userEmail(userEmail)
                .userRoles(userRoles)
                .endpoint(endpoint)
                .allowed(false)
                .message(String.format(
                    "User %s (roles: %s) attempted to access %s (requires: %s)",
                    userEmail, userRoles, endpoint, requiredRole
                ))
                .build();
    }
    
    /**
     * Create an audit event for successful authentication
     */
    public static AuditEvent authenticationSuccess(
            String userExternalId,
            String userEmail,
            String clientIp) {
        
        return AuditEvent.builder()
                .eventType(EventType.AUTHENTICATION_SUCCESS)
                .userExternalId(userExternalId)
                .userEmail(userEmail)
                .clientIp(clientIp)
                .allowed(true)
                .message(String.format("User %s successfully authenticated", userEmail))
                .build();
    }
    
    /**
     * Get a summary string for logging
     */
    public String getSummary() {
        return String.format(
            "[%s] %s | User: %s | Resource: %s | Allowed: %s",
            eventType,
            timestamp,
            userEmail != null ? userEmail : userExternalId,
            resourceType != null ? resourceType : endpoint,
            allowed
        );
    }
}
