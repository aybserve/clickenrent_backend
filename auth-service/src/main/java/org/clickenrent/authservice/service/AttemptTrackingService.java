package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.authservice.entity.EmailVerification;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.repository.EmailVerificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Separate service for tracking verification attempts.
 * Uses REQUIRES_NEW propagation to ensure attempts are persisted
 * even when the parent transaction rolls back.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AttemptTrackingService {

    private final EmailVerificationRepository emailVerificationRepository;

    /**
     * Increment failed attempt counter in a separate transaction.
     * This transaction commits independently, even if the parent transaction rolls back.
     * 
     * @param verificationId Email verification record ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementAttempts(Long verificationId) {
        log.info("=== AttemptTrackingService.incrementAttempts() called for verification ID: {}", verificationId);
        
        EmailVerification verification = emailVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new ResourceNotFoundException("EmailVerification", "id", verificationId));
        
        int oldAttempts = verification.getAttempts();
        log.info("=== Current attempts BEFORE increment: {}", oldAttempts);
        
        verification.setAttempts(oldAttempts + 1);
        log.info("=== Setting attempts to: {}", verification.getAttempts());
        
        EmailVerification saved = emailVerificationRepository.save(verification);
        log.info("=== Saved verification, attempts after save: {}", saved.getAttempts());
        
        // Force flush to database
        emailVerificationRepository.flush();
        log.info("=== Flushed to database");
        
        log.info("=== Successfully incremented attempts from {} to {} for verification ID: {}", 
                oldAttempts, saved.getAttempts(), verificationId);
    }
}







