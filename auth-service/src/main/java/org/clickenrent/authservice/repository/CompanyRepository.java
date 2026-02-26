package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Find company by externalId for cross-service lookups
     */
    java.util.Optional<Company> findByExternalId(String externalId);

    /**
     * Find companies by external IDs (for multi-tenant filtering).
     * Used by B2B admins to query only their companies.
     */
    @Query("SELECT c FROM Company c WHERE c.externalId IN :companyExternalIds")
    List<Company> findByExternalIds(@Param("companyExternalIds") List<String> companyExternalIds);
}


