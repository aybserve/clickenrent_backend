package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.UserGlobalRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for UserGlobalRole entity.
 * Provides standard CRUD operations for managing user global role assignments.
 */
@Repository
public interface UserGlobalRoleRepository extends JpaRepository<UserGlobalRole, Long> {
}

