package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}

