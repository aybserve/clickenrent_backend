package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.CompanyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CompanyRole entity.
 * Provides standard CRUD operations for managing company roles.
 */
@Repository
public interface CompanyRoleRepository extends JpaRepository<CompanyRole, Long> {
}


