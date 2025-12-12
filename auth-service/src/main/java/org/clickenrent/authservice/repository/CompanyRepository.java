package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Company entity.
 * Provides standard CRUD operations for managing companies.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    /**
     * Find companies by list of IDs with pagination.
     * Used for B2B users to fetch only their accessible companies efficiently.
     */
    Page<Company> findByIdIn(List<Long> ids, Pageable pageable);
}


