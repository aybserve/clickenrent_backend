package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.CompanyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for CompanyRole entity.
 * Provides standard CRUD operations for managing company roles.
 */
@Repository
public interface CompanyRoleRepository extends JpaRepository<CompanyRole, Long> {
    
    /**
     * Find a company role by its name (case-insensitive).
     * @param name The role name (e.g., "Owner", "Admin", "Staff")
     * @return Optional containing the role if found
     */
    Optional<CompanyRole> findByNameIgnoreCase(String name);
    
    Optional<CompanyRole> findByExternalId(String externalId);
}


