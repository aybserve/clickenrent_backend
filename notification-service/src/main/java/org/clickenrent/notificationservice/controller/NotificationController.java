package org.clickenrent.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.notification.*;
import org.clickenrent.notificationservice.entity.NotificationLog;
import org.clickenrent.notificationservice.repository.NotificationLogRepository;
import org.clickenrent.notificationservice.service.NotificationService;
import org.clickenrent.notificationservice.service.SecurityService;
import org.clickenrent.notificationservice.service.TokenManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * REST controller for notification operations.
 */
@RestController
@RequestMapping("/api/v1/notifications")
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
    public ResponseEntity<RegisterTokenResponse> registerToken(@Valid @RequestBody RegisterTokenRequest request) {
        String userExternalId = securityService.getCurrentUserExternalId();
        log.info("Registering token for user: {}", userExternalId);
        RegisterTokenResponse response = tokenManagementService.registerToken(userExternalId, request);
        return ResponseEntity.ok(response);
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
            description = "Get paginated notification history for the authenticated user with unread count",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<NotificationHistoryResponse> getHistory(Pageable pageable) {
        String userExternalId = securityService.getCurrentUserExternalId();
        log.info("Fetching notification history for user: {}", userExternalId);
        
        // Get paginated notifications
        Page<NotificationLog> notificationPage = notificationLogRepository.findByUserExternalId(userExternalId, pageable);
        
        // Get unread count
        long unreadCount = notificationLogRepository.countByUserExternalIdAndIsReadFalse(userExternalId);
        
        // Map to DTOs
        var notificationDTOs = notificationPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        // Build response
        NotificationHistoryResponse response = NotificationHistoryResponse.builder()
                .notifications(notificationDTOs)
                .total(notificationPage.getTotalElements())
                .unreadCount(unreadCount)
                .page(notificationPage.getNumber())
                .size(notificationPage.getSize())
                .totalPages(notificationPage.getTotalPages())
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Map NotificationLog entity to NotificationDTO.
     */
    private NotificationDTO mapToDTO(NotificationLog log) {
        return NotificationDTO.builder()
                .id(String.valueOf(log.getId()))
                .type(log.getNotificationType())
                .title(log.getTitle())
                .body(log.getBody())
                .data(log.getData())
                .sentAt(log.getCreatedAt())
                .readAt(log.getReadAt())
                .isRead(log.getIsRead())
                .deliveryStatus(log.getDeliveryStatus() != null ? log.getDeliveryStatus() : log.getStatus())
                .build();
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

    /**
     * Mark a notification as read.
     */
    @PostMapping("/{id}/read")
    @Operation(
            summary = "Mark notification as read",
            description = "Mark a specific notification as read for the authenticated user",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        String userExternalId = securityService.getCurrentUserExternalId();
        log.info("Marking notification {} as read for user: {}", id, userExternalId);
        boolean success = notificationService.markAsRead(id, userExternalId);
        
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Mark all notifications as read for the current user.
     */
    @PostMapping("/read-all")
    @Operation(
            summary = "Mark all notifications as read",
            description = "Mark all notifications as read for the authenticated user",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<Void> markAllAsRead() {
        String userExternalId = securityService.getCurrentUserExternalId();
        log.info("Marking all notifications as read for user: {}", userExternalId);
        int count = notificationService.markAllAsRead(userExternalId);
        log.info("Marked {} notifications as read", count);
        return ResponseEntity.ok().build();
    }
}

