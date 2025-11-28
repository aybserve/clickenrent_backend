package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for User entity.
 * Provides standard CRUD operations for managing users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}

