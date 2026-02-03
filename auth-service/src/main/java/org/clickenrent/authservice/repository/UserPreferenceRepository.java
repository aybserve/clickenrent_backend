package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for UserPreference entity.
 * Provides standard CRUD operations and custom queries for managing user preferences.
 */
@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    
    /**
     * Find user preferences by user ID.
     *
     * @param userId the user ID
     * @return Optional containing the user preferences if found
     */
    Optional<UserPreference> findByUserId(Long userId);
    
    /**
     * Find user preferences by user's external ID.
     * Useful for cross-service communication.
     *
     * @param userExternalId the user's external ID
     * @return Optional containing the user preferences if found
     */
    @Query("SELECT up FROM UserPreference up JOIN up.user u WHERE u.externalId = :userExternalId")
    Optional<UserPreference> findByUserExternalId(@Param("userExternalId") String userExternalId);
    
    /**
     * Check if preferences exist for a given user ID.
     *
     * @param userId the user ID
     * @return true if preferences exist, false otherwise
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Delete user preferences by user ID.
     *
     * @param userId the user ID
     */
    void deleteByUserId(Long userId);
}
