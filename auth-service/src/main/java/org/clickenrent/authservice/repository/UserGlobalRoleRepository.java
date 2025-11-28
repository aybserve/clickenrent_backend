package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserGlobalRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for UserGlobalRole entity.
 * Provides standard CRUD operations for managing user global role assignments.
 */
@Repository
public interface UserGlobalRoleRepository extends JpaRepository<UserGlobalRole, Long> {
    
    /**
     * Find all global roles assigned to a user.
     */
    List<UserGlobalRole> findByUser(User user);
    
    /**
     * Find all global roles assigned to a user by user ID.
     */
    List<UserGlobalRole> findByUserId(Long userId);
}

