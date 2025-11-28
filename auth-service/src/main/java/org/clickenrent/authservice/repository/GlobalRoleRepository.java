package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.GlobalRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for GlobalRole entity.
 * Provides standard CRUD operations for managing global roles.
 */
@Repository
public interface GlobalRoleRepository extends JpaRepository<GlobalRole, Long> {
}

