package org.clickenrent.authservice.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.authservice.entity.PasswordResetToken;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.exception.InvalidVerificationCodeException;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.repository.PasswordResetTokenRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for managing password reset operations.
 * Handles token generation, validation, and password reset with security best practices.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final PasswordResetTokenRepository resetTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${password.reset.token-length:6}")
    private int tokenLength;

    @Value("${password.reset.expiration-minutes:30}")
    private int expirationMinutes;

    @Value("${password.reset.max-attempts:3}")
    private int maxAttempts;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Initiate password reset by sending reset token to user's email.
     * Invalidates any existing unused tokens.
     * 
     * @param email User's email address
     * @param request HTTP request for IP and User-Agent tracking
     */
    @Transactional
    public void initiatePasswordReset(String email, HttpServletRequest request) {
        log.debug("Initiating password reset for email: {}", email);

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Check if user account is active
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            log.warn("Password reset attempted for inactive user: {}", email);
            throw new InvalidVerificationCodeException("User account is not active");
        }

        // Invalidate any existing unused tokens for this user
        Optional<PasswordResetToken> existingToken = 
                resetTokenRepository.findByUserIdAndIsUsedFalseAndIsDeletedFalse(user.getId());

        if (existingToken.isPresent()) {
            log.debug("Found existing reset token (ID: {}) for user ID: {}, soft-deleting it", 
                    existingToken.get().getId(), user.getId());
            try {
                // Use repository.delete() which triggers @SQLDelete annotation
                resetTokenRepository.delete(existingToken.get());
                log.debug("Successfully soft-deleted existing reset token ID: {}", existingToken.get().getId());
            } catch (Exception e) {
                log.error("Failed to soft-delete existing reset token ID: {} for user ID: {}", 
                        existingToken.get().getId(), user.getId(), e);
                throw e;
            }
        } else {
            log.debug("No existing reset token found for user ID: {}", user.getId());
        }

        // Generate new 6-digit token
        String token = generateToken();
        log.debug("Generated reset token for user ID: {}", user.getId());

        // Create new reset token record with IP and User-Agent tracking
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .email(user.getEmail())
                .token(token)
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .ipAddress(getClientIpAddress(request))
                .userAgent(request.getHeader("User-Agent"))
                .attempts(0)
                .isUsed(false)
                .build();

        log.debug("Attempting to save new reset token record for user ID: {}, email: {}", 
                user.getId(), user.getEmail());

        try {
            resetTokenRepository.save(resetToken);
            log.debug("Successfully saved reset token record ID: {} for user ID: {}", 
                    resetToken.getId(), user.getId());
        } catch (Exception e) {
            log.error("Failed to save reset token record for user ID: {}, email: {}", 
                    user.getId(), user.getEmail(), e);
            throw e;
        }

        // Send email with reset token
        emailService.sendPasswordResetEmail(user.getEmail(), token, user.getFirstName());

        log.info("Password reset token generated and sent to user: {} ({})", user.getUserName(), user.getEmail());
    }

    /**
     * Reset user password with token.
     * 
     * @param email User's email address
     * @param token Reset token
     * @param newPassword New password
     * @param request HTTP request for IP and User-Agent tracking
     * @throws InvalidVerificationCodeException if token is invalid
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public void resetPassword(String email, String token, String newPassword, HttpServletRequest request) {
        log.info("===========================================");
        log.info("=== PasswordResetService.resetPassword() CALLED");
        log.info("=== Email: {}", email);
        log.info("=== Token: {}", token);
        log.info("===========================================");

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Find active reset token BY EMAIL ONLY (not by token)
        PasswordResetToken resetToken = resetTokenRepository
                .findByEmailAndIsUsedFalseAndIsDeletedFalse(email)
                .orElseThrow(() -> new InvalidVerificationCodeException(
                        "No password reset request found. Please request a new reset code.", 0));

        log.info("=== FOUND reset token record ID: {} for email: {}", resetToken.getId(), email);
        log.info("=== Stored token: {}, Provided token: {}", resetToken.getToken(), token);
        log.info("=== Current attempts: {}/{}", resetToken.getAttempts(), maxAttempts);
        log.info("=== Tokens match: {}", resetToken.getToken().equals(token));

        // Check if token is expired
        if (isTokenExpired(resetToken)) {
            log.warn("Reset token expired for email: {}", email);
            throw new InvalidVerificationCodeException(
                    "Reset token has expired. Please request a new reset code.", 0);
        }

        // Check if max attempts reached BEFORE comparing tokens
        if (resetToken.getAttempts() >= maxAttempts) {
            log.warn("Max reset attempts ({}) reached for email: {}", maxAttempts, email);
            throw new InvalidVerificationCodeException(
                    "Too many failed attempts. Please request a new reset code.", 0);
        }

        // Validate token format and compare with stored token
        boolean tokenIsValid = token != null && 
                              token.length() == 6 && 
                              token.matches("\\d{6}") && 
                              resetToken.getToken().equals(token);

        if (!tokenIsValid) {
            // Determine specific error message
            String errorMessage;
            if (token == null || token.isEmpty()) {
                errorMessage = "Reset token is required";
            } else if (token.length() != 6) {
                errorMessage = "Reset token must be exactly 6 digits (received " + token.length() + " characters)";
            } else if (!token.matches("\\d{6}")) {
                errorMessage = "Reset token must contain only digits";
            } else {
                errorMessage = "Invalid reset token. Please try again.";
            }

            log.warn("Invalid reset token provided for email: {}, attempts: {}/{}, reason: {}", 
                    email, resetToken.getAttempts() + 1, maxAttempts, errorMessage);

            // Increment failed attempts in a separate transaction
            Long tokenId = resetToken.getId();
            int currentAttempts = resetToken.getAttempts();

            log.info("=== BEFORE incrementing attempts for reset token ID: {}, current attempts: {}", 
                    tokenId, currentAttempts);
            incrementAttempts(tokenId);
            log.info("=== AFTER incrementing attempts for reset token ID: {}", tokenId);

            // Calculate remaining attempts
            int remaining = Math.max(0, maxAttempts - (currentAttempts + 1));
            throw new InvalidVerificationCodeException(errorMessage, remaining);
        }

        log.debug("Reset token matched for email: {}", email);

        // Token is valid - mark as used with tracking info
        resetToken.setIsUsed(true);
        resetToken.setUsedAt(LocalDateTime.now());
        resetToken.setUsedIpAddress(getClientIpAddress(request));
        resetToken.setUsedUserAgent(request.getHeader("User-Agent"));
        resetTokenRepository.save(resetToken);
        log.debug("Reset token record marked as used for email: {}", email);

        // Update user password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.debug("Password updated for user: {}", user.getUserName());

        // Send confirmation email
        emailService.sendPasswordChangedEmail(user.getEmail(), user.getFirstName());

        log.info("Password reset successfully for user: {} ({})", user.getUserName(), user.getEmail());
    }

    /**
     * Increment failed attempt counter in a separate transaction.
     * 
     * @param tokenId Reset token record ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementAttempts(Long tokenId) {
        log.info("=== PasswordResetService.incrementAttempts() called for token ID: {}", tokenId);

        PasswordResetToken resetToken = resetTokenRepository.findById(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException("PasswordResetToken", "id", tokenId));

        int oldAttempts = resetToken.getAttempts();
        log.info("=== Current attempts BEFORE increment: {}", oldAttempts);

        resetToken.setAttempts(oldAttempts + 1);
        log.info("=== Setting attempts to: {}", resetToken.getAttempts());

        PasswordResetToken saved = resetTokenRepository.save(resetToken);
        log.info("=== Saved reset token, attempts after save: {}", saved.getAttempts());

        // Force flush to database
        resetTokenRepository.flush();
        log.info("=== Flushed to database");

        log.info("=== Successfully incremented attempts from {} to {} for reset token ID: {}", 
                oldAttempts, saved.getAttempts(), tokenId);
    }

    /**
     * Check if reset token is expired.
     * 
     * @param resetToken Password reset token record
     * @return true if expired, false otherwise
     */
    private boolean isTokenExpired(PasswordResetToken resetToken) {
        return LocalDateTime.now().isAfter(resetToken.getExpiresAt());
    }

    /**
     * Generate random 6-digit numeric token.
     * 
     * @return 6-digit token as string
     */
    private String generateToken() {
        int token = RANDOM.nextInt(900000) + 100000; // Generates number between 100000 and 999999
        return String.valueOf(token);
    }

    /**
     * Get remaining attempts for a reset token.
     * 
     * @param resetToken Password reset token record
     * @return Number of attempts remaining
     */
    public int getRemainingAttempts(PasswordResetToken resetToken) {
        return Math.max(0, maxAttempts - resetToken.getAttempts());
    }

    /**
     * Extract client IP address from HTTP request.
     * Handles X-Forwarded-For header for proxied requests.
     * 
     * @param request HTTP request
     * @return Client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Validate password reset token without consuming attempts.
     * 
     * @param token Reset token (optional)
     * @param email User's email address
     * @return Token validation response with status and details
     */
    public org.clickenrent.authservice.dto.TokenValidationResponse validateToken(String token, String email) {
        log.debug("Validating reset token for email: {}", email);

        // Find active reset token by email
        Optional<PasswordResetToken> tokenOpt = 
                resetTokenRepository.findByEmailAndIsUsedFalseAndIsDeletedFalse(email);

        if (tokenOpt.isEmpty()) {
            return org.clickenrent.authservice.dto.TokenValidationResponse.builder()
                    .valid(false)
                    .message("No password reset request found")
                    .build();
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // Check expiration
        if (isTokenExpired(resetToken)) {
            return org.clickenrent.authservice.dto.TokenValidationResponse.builder()
                    .valid(false)
                    .message("Reset token has expired")
                    .expiresAt(resetToken.getExpiresAt())
                    .build();
        }

        // Check max attempts
        if (resetToken.getAttempts() >= maxAttempts) {
            return org.clickenrent.authservice.dto.TokenValidationResponse.builder()
                    .valid(false)
                    .message("Maximum attempts exceeded")
                    .remainingAttempts(0)
                    .build();
        }

        // Token is valid
        return org.clickenrent.authservice.dto.TokenValidationResponse.builder()
                .valid(true)
                .message("Token is valid")
                .expiresAt(resetToken.getExpiresAt())
                .remainingAttempts(getRemainingAttempts(resetToken))
                .build();
    }
}
