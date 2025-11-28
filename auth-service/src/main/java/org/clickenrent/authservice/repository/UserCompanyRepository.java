package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.UserCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for UserCompany entity.
 * Provides standard CRUD operations for managing user-company relationships.
 */
@Repository
public interface UserCompanyRepository extends JpaRepository<UserCompany, Long> {
}

