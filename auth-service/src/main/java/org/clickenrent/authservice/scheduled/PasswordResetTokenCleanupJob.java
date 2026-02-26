package org.clickenrent.authservice.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.authservice.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Scheduled job to cleanup expired and used password reset tokens.
 * Runs daily at 2 AM by default (configurable via cron expression).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PasswordResetTokenCleanupJob {

    private final PasswordResetTokenRepository tokenRepository;

    @Value("${password.reset.cleanup.retention-days:7}")
    private int retentionDays;

    /**
     * Clean up expired password reset tokens.
     * Deletes tokens that are:
     * - Expired (expiresAt is in the past)
     * - Used and older than retention period (isUsed = true and usedAt is older than retention)
     */
    @Scheduled(cron = "${password.reset.cleanup.cron:0 0 2 * * *}")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Starting password reset token cleanup job");

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);

        try {
            int deleted = tokenRepository.deleteExpiredTokens(cutoffDate);
            log.info("Password reset token cleanup completed. Deleted {} expired tokens older than {}",
                    deleted, cutoffDate);
        } catch (Exception e) {
            log.error("Failed to cleanup expired password reset tokens", e);
        }
    }
}
