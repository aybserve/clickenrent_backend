package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserAddress;
import org.clickenrent.authservice.entity.UserCompany;
import org.clickenrent.authservice.repository.UserAddressRepository;
import org.clickenrent.authservice.repository.UserCompanyRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Service for handling security and permission checks.
 * Used for fine-grained authorization logic.
 */
@Service
@RequiredArgsConstructor
public class SecurityService {
    
    private final UserRepository userRepository;
    private final UserCompanyRepository userCompanyRepository;
    private final UserAddressRepository userAddressRepository;
    
    /**
     * Check if current user is SuperAdmin or Admin.
     */
    public boolean isAdmin() {
        return hasAnyRole("ROLE_SUPERADMIN", "ROLE_ADMIN");
    }
    
    /**
     * Check if current user is B2B.
     */
    public boolean isB2B() {
        return hasRole("ROLE_B2B");
    }
    
    /**
     * Check if current user is Customer.
     */
    public boolean isCustomer() {
        return hasRole("ROLE_CUSTOMER");
    }
    
    /**
     * Check if current user has access to a specific company.
     * - SUPERADMIN/ADMIN: always true
     * - B2B: only if they belong to the company
     * - CUSTOMER: false
     */
    public boolean hasAccessToCompany(Long companyId) {
        if (isAdmin()) {
            return true;
        }
        
        if (isB2B()) {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return false;
            }
            
            List<UserCompany> userCompanies = userCompanyRepository.findByUserId(currentUser.getId());
            return userCompanies.stream()
                    .anyMatch(uc -> uc.getCompany().getId().equals(companyId));
        }
        
        return false;
    }
    
    /**
     * Check if current user has access to view another user's information.
     * - SUPERADMIN/ADMIN: can see all users
     * - B2B: can only see users in their companies
     * - CUSTOMER: can only see themselves
     */
    public boolean hasAccessToUser(Long userId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        // Admin can see everyone
        if (isAdmin()) {
            return true;
        }
        
        // Customer can only see themselves
        if (isCustomer()) {
            return currentUser.getId().equals(userId);
        }
        
        // B2B can see users in their companies
        if (isB2B()) {
            // If checking their own profile
            if (currentUser.getId().equals(userId)) {
                return true;
            }
            
            // Check if the target user is in any of the same companies
            List<UserCompany> currentUserCompanies = userCompanyRepository.findByUserId(currentUser.getId());
            List<UserCompany> targetUserCompanies = userCompanyRepository.findByUserId(userId);
            
            return currentUserCompanies.stream()
                    .anyMatch(cuc -> targetUserCompanies.stream()
                            .anyMatch(tuc -> tuc.getCompany().getId().equals(cuc.getCompany().getId())));
        }
        
        return false;
    }
    
    /**
     * Get list of company IDs that current user has access to.
     * - SUPERADMIN/ADMIN: all companies (returns null to indicate all)
     * - B2B: only their companies
     * - CUSTOMER: empty list
     */
    public List<Long> getAccessibleCompanyIds() {
        if (isAdmin()) {
            return null; // null means all companies
        }
        
        if (isB2B()) {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return List.of();
            }
            
            List<UserCompany> userCompanies = userCompanyRepository.findByUserId(currentUser.getId());
            return userCompanies.stream()
                    .map(uc -> uc.getCompany().getId())
                    .toList();
        }
        
        return List.of(); // Customers have no company access
    }
    
    /**
     * Check if current user has access to a specific address.
     * - SUPERADMIN/ADMIN: always true
     * - B2B/CUSTOMER: only if they are linked to the address via UserAddress
     */
    public boolean hasAccessToAddress(Long addressId) {
        if (isAdmin()) {
            return true;
        }
        
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        // Check if user has this address in their UserAddress relationships
        List<UserAddress> userAddresses = userAddressRepository.findByUserId(currentUser.getId());
        return userAddresses.stream()
                .anyMatch(ua -> ua.getAddress().getId().equals(addressId));
    }
    
    /**
     * Get list of address IDs that current user has access to.
     * - SUPERADMIN/ADMIN: all addresses (returns null to indicate all)
     * - B2B/CUSTOMER: only their addresses
     */
    public List<Long> getAccessibleAddressIds() {
        if (isAdmin()) {
            return null; // null means all addresses
        }
        
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        
        List<UserAddress> userAddresses = userAddressRepository.findByUserId(currentUser.getId());
        return userAddresses.stream()
                .map(ua -> ua.getAddress().getId())
                .toList();
    }
    
    /**
     * Check if current user can invite users to a company.
     * - SUPERADMIN/ADMIN: can invite to any company
     * - B2B: can invite to companies they belong to
     * - CUSTOMER: cannot invite
     * 
     * @param companyId The company ID to check invitation permission for
     * @return true if user can invite to the company
     */
    public boolean canInviteToCompany(Long companyId) {
        if (isAdmin()) {
            return true;
        }
        
        if (isB2B()) {
            return hasAccessToCompany(companyId);
        }
        
        return false; // CUSTOMER cannot invite
    }
    
    /**
     * Get current authenticated user.
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        String username = authentication.getName();
        return userRepository.findByUserName(username)
                .orElse(null);
    }
    
    /**
     * Get current user's ID.
     */
    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
    
    /**
     * Check if current user has a specific role.
     */
    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(role));
    }
    
    /**
     * Check if current user has any of the specified roles.
     */
    private boolean hasAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (String role : roles) {
            if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals(role))) {
                return true;
            }
        }
        return false;
    }
}


