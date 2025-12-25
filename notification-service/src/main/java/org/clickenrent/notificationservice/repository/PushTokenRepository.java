package org.clickenrent.notificationservice.repository;

import org.clickenrent.notificationservice.entity.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PushToken entity.
 */
@Repository
public interface PushTokenRepository extends JpaRepository<PushToken, Long> {

    /**
     * Find all active tokens for a specific user
     */
    List<PushToken> findByUserExternalIdAndIsActiveTrue(String userExternalId);

    /**
     * Find a token by its Expo push token string
     */
    Optional<PushToken> findByExpoPushToken(String expoPushToken);

    /**
     * Find all tokens for a user (active and inactive)
     */
    List<PushToken> findByUserExternalId(String userExternalId);

    /**
     * Check if a token exists
     */
    boolean existsByExpoPushToken(String expoPushToken);
}

