package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides standard CRUD operations for managing users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUserName(String userName);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByExternalId(String externalId);
    
    Optional<User> findByProviderIdAndProviderUserId(String providerId, String providerUserId);
    
    boolean existsByUserName(String userName);
}

