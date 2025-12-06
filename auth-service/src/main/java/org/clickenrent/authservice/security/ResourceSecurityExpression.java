package org.clickenrent.authservice.security;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.service.SecurityService;
import org.springframework.stereotype.Component;

/**
 * Custom SpEL security expressions for resource-based authorization.
 * Used in @PreAuthorize annotations to check if current user can access specific resources.
 * 
 * Usage example:
 * @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessAddress(#id)")
 * 
 * Pattern for adding new resources:
 * 1. Add hasAccessToXxx() method to SecurityService
 * 2. Add canAccessXxx() method to this class
 * 3. Use in @PreAuthorize: hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessXxx(#id)
 */
@Component("resourceSecurity")
@RequiredArgsConstructor
public class ResourceSecurityExpression {
    
    private final SecurityService securityService;
    
    /**
     * Generic method to check resource access based on resource type.
     * 
     * @param resourceId The ID of the resource to check
     * @param resourceType The type of resource (e.g., "ADDRESS", "VEHICLE", "COMPANY")
     * @return true if current user can access the resource, false otherwise
     */
    public boolean canAccessResource(Long resourceId, String resourceType) {
        if (resourceId == null || resourceType == null) {
            return false;
        }
        
        // Admin bypass - SUPERADMIN and ADMIN can access everything
        if (securityService.isAdmin()) {
            return true;
        }
        
        // Route to specific resource checker based on type
        return switch (resourceType.toUpperCase()) {
            case "ADDRESS" -> securityService.hasAccessToAddress(resourceId);
            case "COMPANY" -> securityService.hasAccessToCompany(resourceId);
            case "USER" -> securityService.hasAccessToUser(resourceId);
            // Add more resource types here as needed:
            // case "VEHICLE" -> securityService.hasAccessToVehicle(resourceId);
            // case "BOOKING" -> securityService.hasAccessToBooking(resourceId);
            default -> false; // Unknown resource type, deny access
        };
    }
    
    /**
     * Check if current user can access a specific address.
     * - SUPERADMIN/ADMIN: always true
     * - B2B/CUSTOMER: true only if they own the address (via UserAddress)
     * 
     * @param addressId The address ID to check
     * @return true if user can access the address
     */
    public boolean canAccessAddress(Long addressId) {
        if (addressId == null) {
            return false;
        }
        return securityService.hasAccessToAddress(addressId);
    }
    
    /**
     * Check if current user can access a specific company.
     * - SUPERADMIN/ADMIN: always true
     * - B2B: true only if they belong to the company
     * - CUSTOMER: false
     * 
     * @param companyId The company ID to check
     * @return true if user can access the company
     */
    public boolean canAccessCompany(Long companyId) {
        if (companyId == null) {
            return false;
        }
        return securityService.hasAccessToCompany(companyId);
    }
    
    /**
     * Check if current user can access another user's information.
     * - SUPERADMIN/ADMIN: always true
     * - B2B: true for users in their companies
     * - CUSTOMER: true only for themselves
     * 
     * @param userId The user ID to check
     * @return true if user can access the other user's info
     */
    public boolean canAccessUser(Long userId) {
        if (userId == null) {
            return false;
        }
        return securityService.hasAccessToUser(userId);
    }
    
    /**
     * Check if current user is the owner of a resource (matches their user ID).
     * Useful for endpoints where users can only access their own data.
     * 
     * @param userId The user ID to check against current user
     * @return true if current user's ID matches the provided userId
     */
    public boolean isOwner(Long userId) {
        if (userId == null) {
            return false;
        }
        Long currentUserId = securityService.getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }
    
    /**
     * Check if current user can invite users to a company.
     * - SUPERADMIN/ADMIN: can invite to any company
     * - B2B: can invite to companies they belong to
     * - CUSTOMER: cannot invite
     * 
     * @param companyId The company ID to check
     * @return true if user can invite to the company
     */
    public boolean canInviteToCompany(Long companyId) {
        if (companyId == null) {
            return false;
        }
        return securityService.canInviteToCompany(companyId);
    }
}

