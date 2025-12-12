package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Company entity.
 * Provides standard CRUD operations for managing companies.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
}


