package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserCompany entity.
 * Provides standard CRUD operations for managing user-company relationships.
 */
@Repository
public interface UserCompanyRepository extends JpaRepository<UserCompany, Long> {
    
    /**
     * Find all company associations for a user.
     */
    List<UserCompany> findByUser(User user);
    
    /**
     * Find all company associations for a user by user ID.
     */
    List<UserCompany> findByUserId(Long userId);
    
    /**
     * Find all user associations for a company by company ID.
     */
    List<UserCompany> findByCompanyId(Long companyId);
    
    Optional<UserCompany> findByExternalId(String externalId);

    /**
     * Find user-company associations filtered by company external IDs (for multi-tenant filtering).
     * Used by B2B admins to query only users in their companies.
     */
    @Query("SELECT uc FROM UserCompany uc WHERE uc.company.externalId IN :companyExternalIds")
    List<UserCompany> findByCompanyExternalIds(@Param("companyExternalIds") List<String> companyExternalIds);
}

