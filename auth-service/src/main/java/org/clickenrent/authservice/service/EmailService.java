package org.clickenrent.authservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Mock email service for sending verification codes.
 * Logs emails to console instead of actually sending them.
 * 
 * TODO: Replace with real SMTP implementation using Spring Mail in production.
 */
@Service
@Slf4j
public class EmailService {

    /**
     * Send verification email with 6-digit code.
     * Currently logs to console for development/testing.
     * 
     * @param to Recipient email address
     * @param code 6-digit verification code
     */
    public void sendVerificationEmail(String to, String code) {
        log.info("===========================================");
        log.info("MOCK EMAIL SERVICE - Verification Code");
        log.info("To: {}", to);
        log.info("Subject: Verify Your Email Address");
        log.info("===========================================");
        log.info("");
        log.info("Hello,");
        log.info("");
        log.info("Your email verification code is: {}", code);
        log.info("");
        log.info("This code will expire in 15 minutes.");
        log.info("Please do not share this code with anyone.");
        log.info("");
        log.info("If you did not request this code, please ignore this email.");
        log.info("");
        log.info("===========================================");
    }

    /**
     * Send welcome email after successful verification.
     * Currently logs to console for development/testing.
     * 
     * @param to Recipient email address
     * @param firstName User's first name
     */
    public void sendWelcomeEmail(String to, String firstName) {
        log.info("===========================================");
        log.info("MOCK EMAIL SERVICE - Welcome Email");
        log.info("To: {}", to);
        log.info("Subject: Welcome to ClickenRent!");
        log.info("===========================================");
        log.info("");
        log.info("Hello {},", firstName);
        log.info("");
        log.info("Welcome to ClickenRent! Your email has been successfully verified.");
        log.info("You can now enjoy all the features of our platform.");
        log.info("");
        log.info("Thank you for joining us!");
        log.info("");
        log.info("===========================================");
    }
}
