package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.CompanyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CompanyType entity.
 * Provides standard CRUD operations for managing company types.
 */
@Repository
public interface CompanyTypeRepository extends JpaRepository<CompanyType, Long> {
}






