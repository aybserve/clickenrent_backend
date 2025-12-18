package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for EmailVerification entity.
 */
@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    /**
     * Find active (unused, not deleted) verification by user ID.
     */
    Optional<EmailVerification> findByUserIdAndIsUsedFalseAndIsDeletedFalse(Long userId);

    /**
     * Find active verification by email and code.
     */
    Optional<EmailVerification> findByEmailAndCodeAndIsUsedFalseAndIsDeletedFalse(String email, String code);

    /**
     * Find the most recent verification for a user (including used and deleted).
     */
    @Query("SELECT ev FROM EmailVerification ev WHERE ev.user.id = :userId ORDER BY ev.dateCreated DESC")
    Optional<EmailVerification> findLatestByUserId(@Param("userId") Long userId);

    /**
     * Find active verification by email.
     */
    Optional<EmailVerification> findByEmailAndIsUsedFalseAndIsDeletedFalse(String email);
}


