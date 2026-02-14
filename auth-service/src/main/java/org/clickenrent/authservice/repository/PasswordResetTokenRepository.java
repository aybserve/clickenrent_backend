package org.clickenrent.authservice.repository;

import org.clickenrent.authservice.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for PasswordResetToken entity.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Find active (unused, not deleted) reset token by user ID.
     */
    Optional<PasswordResetToken> findByUserIdAndIsUsedFalseAndIsDeletedFalse(Long userId);

    /**
     * Find active reset token by email.
     */
    Optional<PasswordResetToken> findByEmailAndIsUsedFalseAndIsDeletedFalse(String email);

    /**
     * Find active reset token by email and token.
     */
    Optional<PasswordResetToken> findByEmailAndTokenAndIsUsedFalseAndIsDeletedFalse(String email, String token);

    /**
     * Find the most recent reset token for a user (including used and deleted).
     */
    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.user.id = :userId ORDER BY prt.dateCreated DESC")
    Optional<PasswordResetToken> findLatestByUserId(@Param("userId") Long userId);

    /**
     * Delete expired or used tokens older than the specified date.
     * Used for scheduled cleanup job.
     * 
     * @param before Cutoff date - tokens older than this will be deleted
     * @return Number of tokens deleted
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiresAt < :before OR (t.isUsed = true AND t.usedAt < :before)")
    int deleteExpiredTokens(@Param("before") LocalDateTime before);
}
