package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.GlobalRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for GlobalRole entity.
 * Provides standard CRUD operations for managing global roles.
 */
@Repository
public interface GlobalRoleRepository extends JpaRepository<GlobalRole, Long> {
    
    /**
     * Find a global role by its name.
     * @param name The role name (e.g., "CUSTOMER", "ADMIN", "B2B", "SUPERADMIN")
     * @return Optional containing the role if found
     */
    Optional<GlobalRole> findByNameIgnoreCase(String name);
    
    Optional<GlobalRole> findByExternalId(String externalId);
}


