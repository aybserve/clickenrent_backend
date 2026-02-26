package org.clickenrent.notificationservice.repository;

import org.clickenrent.notificationservice.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for NotificationPreference entity.
 */
@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    /**
     * Find preferences by user external ID
     */
    Optional<NotificationPreference> findByUserExternalId(String userExternalId);

    /**
     * Check if preferences exist for a user
     */
    boolean existsByUserExternalId(String userExternalId);
}

