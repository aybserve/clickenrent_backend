package org.clickenrent.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.notification.RegisterTokenRequest;
import org.clickenrent.notificationservice.entity.PushToken;
import org.clickenrent.notificationservice.repository.PushTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing push tokens.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenManagementService {

    private final PushTokenRepository pushTokenRepository;
    private final ExpoPushService expoPushService;

    /**
     * Register or update a push token for a user.
     *
     * @param userExternalId  User external ID
     * @param request Token registration request
     */
    @Transactional
    public void registerToken(String userExternalId, RegisterTokenRequest request) {
        log.info("Registering push token for user: {}", userExternalId);

        // Validate token format
        if (!expoPushService.isValidExpoToken(request.getExpoPushToken())) {
            log.warn("Invalid Expo token format: {}", request.getExpoPushToken());
            throw new IllegalArgumentException("Invalid Expo Push Token format");
        }

        // Check if token already exists
        Optional<PushToken> existingToken = pushTokenRepository.findByExpoPushToken(request.getExpoPushToken());

        if (existingToken.isPresent()) {
            // Update existing token
            PushToken token = existingToken.get();
            token.setUserExternalId(userExternalId);
            token.setDeviceType(request.getDeviceType());
            token.setDeviceName(request.getDeviceName());
            token.setIsActive(true);
            token.setLastUsedAt(LocalDateTime.now());
            pushTokenRepository.save(token);
            log.info("Updated existing push token for user: {}", userExternalId);
        } else {
            // Create new token
            PushToken token = PushToken.builder()
                    .userExternalId(userExternalId)
                    .expoPushToken(request.getExpoPushToken())
                    .deviceType(request.getDeviceType())
                    .deviceName(request.getDeviceName())
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .lastUsedAt(LocalDateTime.now())
                    .isDeleted(false)
                    .build();
            pushTokenRepository.save(token);
            log.info("Created new push token for user: {}", userExternalId);
        }
    }

    /**
     * Get all active tokens for a user.
     *
     * @param userExternalId User external ID
     * @return List of active push tokens
     */
    @Transactional(readOnly = true)
    public List<PushToken> getActiveTokensForUser(String userExternalId) {
        return pushTokenRepository.findByUserExternalIdAndIsActiveTrue(userExternalId);
    }

    /**
     * Deactivate a token (e.g., when Expo reports DeviceNotRegistered error).
     *
     * @param expoPushToken Token to deactivate
     */
    @Transactional
    public void deactivateToken(String expoPushToken) {
        Optional<PushToken> tokenOpt = pushTokenRepository.findByExpoPushToken(expoPushToken);
        if (tokenOpt.isPresent()) {
            PushToken token = tokenOpt.get();
            token.setIsActive(false);
            pushTokenRepository.save(token);
            log.info("Deactivated push token: {}", expoPushToken);
        }
    }

    /**
     * Delete a token for a user.
     *
     * @param userExternalId User external ID
     * @param expoPushToken Token to delete
     */
    @Transactional
    public void deleteToken(String userExternalId, String expoPushToken) {
        Optional<PushToken> tokenOpt = pushTokenRepository.findByExpoPushToken(expoPushToken);
        if (tokenOpt.isPresent()) {
            PushToken token = tokenOpt.get();
            if (token.getUserExternalId().equals(userExternalId)) {
                pushTokenRepository.delete(token);
                log.info("Deleted push token for user: {}", userExternalId);
            } else {
                log.warn("Token does not belong to user: {}", userExternalId);
                throw new IllegalArgumentException("Token does not belong to this user");
            }
        }
    }

    /**
     * Get all tokens for a user (active and inactive).
     *
     * @param userExternalId User external ID
     * @return List of all tokens
     */
    @Transactional(readOnly = true)
    public List<PushToken> getAllTokensForUser(String userExternalId) {
        return pushTokenRepository.findByUserExternalId(userExternalId);
    }
}

