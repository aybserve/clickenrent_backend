package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.authservice.entity.EmailVerification;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.exception.InvalidVerificationCodeException;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.repository.EmailVerificationRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for managing email verification codes.
 * Handles code generation, validation, and attempt tracking.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AttemptTrackingService attemptTrackingService;

    @Value("${email.verification.code-length:6}")
    private int codeLength;

    @Value("${email.verification.expiration-minutes:15}")
    private int expirationMinutes;

    @Value("${email.verification.max-attempts:3}")
    private int maxAttempts;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generate and send verification code to user.
     * Invalidates any existing unused codes.
     * 
     * @param user User to send verification code to
     */
    @Transactional
    public void generateAndSendCode(User user) {
        log.debug("Starting generateAndSendCode for user ID: {}, email: {}", user.getId(), user.getEmail());
        
        // Invalidate any existing unused codes for this user
        Optional<EmailVerification> existingVerification = 
                emailVerificationRepository.findByUserIdAndIsUsedFalseAndIsDeletedFalse(user.getId());
        
        if (existingVerification.isPresent()) {
            log.debug("Found existing verification (ID: {}) for user ID: {}, soft-deleting it", 
                    existingVerification.get().getId(), user.getId());
            try {
                // Use repository.delete() which triggers @SQLDelete annotation
                emailVerificationRepository.delete(existingVerification.get());
                log.debug("Successfully soft-deleted existing verification ID: {}", existingVerification.get().getId());
            } catch (Exception e) {
                log.error("Failed to soft-delete existing verification ID: {} for user ID: {}", 
                        existingVerification.get().getId(), user.getId(), e);
                throw e;
            }
        } else {
            log.debug("No existing verification found for user ID: {}", user.getId());
        }

        // Generate new 6-digit code
        String code = generateCode();
        log.debug("Generated verification code for user ID: {}", user.getId());

        // Create new verification record
        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .email(user.getEmail())
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .attempts(0)
                .isUsed(false)
                .build();

        log.debug("Attempting to save new verification record for user ID: {}, email: {}", 
                user.getId(), user.getEmail());
        
        try {
            emailVerificationRepository.save(verification);
            log.debug("Successfully saved verification record ID: {} for user ID: {}", 
                    verification.getId(), user.getId());
        } catch (Exception e) {
            log.error("Failed to save verification record for user ID: {}, email: {}", 
                    user.getId(), user.getEmail(), e);
            throw e;
        }

        // Send email with code
        emailService.sendVerificationEmail(user.getEmail(), code);

        log.info("Verification code generated and sent to user: {} ({})", user.getUserName(), user.getEmail());
    }

    /**
     * Verify email with code and update user.
     * 
     * @param email User's email address
     * @param code Verification code
     * @return Verified user
     * @throws InvalidVerificationCodeException if code is invalid
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public User verifyEmail(String email, String code) {
        log.info("===========================================");
        log.info("=== EmailVerificationService.verifyEmail() CALLED");
        log.info("=== Email: {}", email);
        log.info("=== Code: {}", code);
        log.info("===========================================");
        
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Check if already verified
        if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
            log.warn("Email verification attempted for already verified user: {}", email);
            throw new InvalidVerificationCodeException("Email is already verified");
        }

        // Find active verification BY EMAIL ONLY (not by code)
        EmailVerification verification = emailVerificationRepository
                .findByEmailAndIsUsedFalseAndIsDeletedFalse(email)
                .orElseThrow(() -> new InvalidVerificationCodeException(
                        "No verification code found. Please request a new code.", 0));

        log.info("=== FOUND verification record ID: {} for email: {}", verification.getId(), email);
        log.info("=== Stored code: {}, Provided code: {}", verification.getCode(), code);
        log.info("=== Current attempts: {}/{}", verification.getAttempts(), maxAttempts);
        log.info("=== Codes match: {}", verification.getCode().equals(code));

        // Check if code is expired
        if (isCodeExpired(verification)) {
            log.warn("Verification code expired for email: {}", email);
            throw new InvalidVerificationCodeException(
                    "Verification code has expired. Please request a new code.", 0);
        }

        // Check if max attempts reached BEFORE comparing codes
        if (verification.getAttempts() >= maxAttempts) {
            log.warn("Max verification attempts ({}) reached for email: {}", maxAttempts, email);
            throw new InvalidVerificationCodeException(
                    "Too many failed attempts. Please request a new code.", 0);
        }

        // Validate code format and compare with stored code
        // We check AFTER loading verification so we can track ALL failed attempts
        boolean codeIsValid = code != null && 
                              code.length() == 6 && 
                              code.matches("\\d{6}") && 
                              verification.getCode().equals(code);
        
        if (!codeIsValid) {
            // Determine specific error message
            String errorMessage;
            if (code == null || code.isEmpty()) {
                errorMessage = "Verification code is required";
            } else if (code.length() != 6) {
                errorMessage = "Verification code must be exactly 6 digits (received " + code.length() + " characters)";
            } else if (!code.matches("\\d{6}")) {
                errorMessage = "Verification code must contain only digits";
            } else {
                errorMessage = "Invalid verification code. Please try again.";
            }
            
            log.warn("Invalid verification code provided for email: {}, attempts: {}/{}, reason: {}", 
                    email, verification.getAttempts() + 1, maxAttempts, errorMessage);
            
            // Increment failed attempts in a separate transaction
            // This tracks ALL failed attempts, regardless of code format
            Long verificationId = verification.getId();
            int currentAttempts = verification.getAttempts();
            
            log.info("=== BEFORE calling attemptTrackingService.incrementAttempts() for verification ID: {}, current attempts: {}", 
                    verificationId, currentAttempts);
            attemptTrackingService.incrementAttempts(verificationId);
            log.info("=== AFTER calling attemptTrackingService.incrementAttempts() for verification ID: {}", verificationId);
            
            // Calculate remaining attempts (current + 1 because we just incremented)
            int remaining = Math.max(0, maxAttempts - (currentAttempts + 1));
            throw new InvalidVerificationCodeException(errorMessage, remaining);
        }

        log.debug("Verification code matched for email: {}", email);

        // Code is valid - mark as used
        verification.setIsUsed(true);
        verification.setUsedAt(LocalDateTime.now());
        emailVerificationRepository.save(verification);
        log.debug("Verification record marked as used for email: {}", email);

        // Update user email verification status
        user.setIsEmailVerified(true);
        user = userRepository.save(user);
        log.debug("User email verification status updated for user: {}", user.getUserName());

        // Send welcome email
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());

        log.info("Email verified successfully for user: {} ({})", user.getUserName(), user.getEmail());

        return user;
    }

    /**
     * Resend verification code to user.
     * Invalidates old code and generates new one.
     * 
     * @param email User's email address
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Check if already verified
        if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
            throw new InvalidVerificationCodeException("Email is already verified");
        }

        // Generate and send new code
        generateAndSendCode(user);

        log.info("Verification code resent to user: {} ({})", user.getUserName(), user.getEmail());
    }

    /**
     * Increment failed attempt counter.
     * Delegates to AttemptTrackingService which uses REQUIRES_NEW propagation.
     * 
     * @param verificationId Email verification record ID
     */
    public void incrementAttempts(Long verificationId) {
        attemptTrackingService.incrementAttempts(verificationId);
    }

    /**
     * Check if verification code is expired.
     * 
     * @param verification Email verification record
     * @return true if expired, false otherwise
     */
    private boolean isCodeExpired(EmailVerification verification) {
        return LocalDateTime.now().isAfter(verification.getExpiresAt());
    }

    /**
     * Generate random 6-digit numeric code.
     * 
     * @return 6-digit code as string
     */
    private String generateCode() {
        int code = RANDOM.nextInt(900000) + 100000; // Generates number between 100000 and 999999
        return String.valueOf(code);
    }

    /**
     * Get remaining attempts for a verification code.
     * 
     * @param verification Email verification record
     * @return Number of attempts remaining
     */
    public int getRemainingAttempts(EmailVerification verification) {
        return Math.max(0, maxAttempts - verification.getAttempts());
    }
}







