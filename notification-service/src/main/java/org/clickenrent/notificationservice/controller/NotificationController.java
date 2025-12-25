package org.clickenrent.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.notification.RegisterTokenRequest;
import org.clickenrent.contracts.notification.SendNotificationRequest;
import org.clickenrent.contracts.notification.SendNotificationResponse;
import org.clickenrent.notificationservice.entity.NotificationLog;
import org.clickenrent.notificationservice.repository.NotificationLogRepository;
import org.clickenrent.notificationservice.service.NotificationService;
import org.clickenrent.notificationservice.service.SecurityService;
import org.clickenrent.notificationservice.service.TokenManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for notification operations.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "Push notification management")
public class NotificationController {

    private final NotificationService notificationService;
    private final TokenManagementService tokenManagementService;
    private final SecurityService securityService;
    private final NotificationLogRepository notificationLogRepository;

    /**
     * Register a push token for the current user.
     * This endpoint is called by the mobile app after obtaining an Expo Push Token.
     */
    @PostMapping("/register-token")
    @Operation(
            summary = "Register push token",
            description = "Register an Expo Push Token for the authenticated user's device",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<Void> registerToken(@Valid @RequestBody RegisterTokenRequest request) {
        String userExternalId = securityService.getCurrentUserExternalId();
        log.info("Registering token for user: {}", userExternalId);
        tokenManagementService.registerToken(userExternalId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * Internal endpoint for other microservices to send notifications.
     * This endpoint should not be exposed through the API gateway or should require service-to-service auth.
     */
    @PostMapping("/internal/send")
    @Operation(
            summary = "Send notification (Internal)",
            description = "Internal endpoint for microservices to send push notifications"
    )
    public ResponseEntity<SendNotificationResponse> sendNotification(
            @Valid @RequestBody SendNotificationRequest request
    ) {
        log.info("Received internal notification request for user: {}, type: {}",
                request.getUserExternalId(), request.getNotificationType());
        SendNotificationResponse response = notificationService.send(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get notification history for the current user.
     */
    @GetMapping("/history")
    @Operation(
            summary = "Get notification history",
            description = "Get paginated notification history for the authenticated user",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<Page<NotificationLog>> getHistory(Pageable pageable) {
        String userExternalId = securityService.getCurrentUserExternalId();
        log.info("Fetching notification history for user: {}", userExternalId);
        Page<NotificationLog> history = notificationLogRepository.findByUserExternalId(userExternalId, pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * Delete a push token for the current user.
     */
    @DeleteMapping("/tokens/{token}")
    @Operation(
            summary = "Delete push token",
            description = "Delete a specific push token for the authenticated user",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<Void> deleteToken(@PathVariable String token) {
        String userExternalId = securityService.getCurrentUserExternalId();
        log.info("Deleting token for user: {}", userExternalId);
        tokenManagementService.deleteToken(userExternalId, token);
        return ResponseEntity.ok().build();
    }
}

