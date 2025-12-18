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
        // Invalidate any existing unused codes for this user
        Optional<EmailVerification> existingVerification = 
                emailVerificationRepository.findByUserIdAndIsUsedFalseAndIsDeletedFalse(user.getId());
        
        existingVerification.ifPresent(verification -> {
            verification.setIsDeleted(true);
            emailVerificationRepository.save(verification);
        });

        // Generate new 6-digit code
        String code = generateCode();

        // Create new verification record
        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .email(user.getEmail())
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .attempts(0)
                .isUsed(false)
                .build();

        emailVerificationRepository.save(verification);

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
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Check if already verified
        if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
            throw new InvalidVerificationCodeException("Email is already verified");
        }

        // Find active verification
        EmailVerification verification = emailVerificationRepository
                .findByEmailAndCodeAndIsUsedFalseAndIsDeletedFalse(email, code)
                .orElseThrow(() -> new InvalidVerificationCodeException(
                        "Verification code is invalid or expired", 0));

        // Check if code is expired
        if (isCodeExpired(verification)) {
            throw new InvalidVerificationCodeException(
                    "Verification code has expired. Please request a new code.", 0);
        }

        // Check if max attempts reached
        if (verification.getAttempts() >= maxAttempts) {
            throw new InvalidVerificationCodeException(
                    "Too many failed attempts. Please request a new code.", 0);
        }

        // Code is valid - mark as used
        verification.setIsUsed(true);
        verification.setUsedAt(LocalDateTime.now());
        emailVerificationRepository.save(verification);

        // Update user email verification status
        user.setIsEmailVerified(true);
        user = userRepository.save(user);

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
     * 
     * @param verification Email verification record
     */
    @Transactional
    public void incrementAttempts(EmailVerification verification) {
        verification.setAttempts(verification.getAttempts() + 1);
        emailVerificationRepository.save(verification);
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

