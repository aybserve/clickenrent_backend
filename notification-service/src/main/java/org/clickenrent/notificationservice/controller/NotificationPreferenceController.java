package org.clickenrent.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.notification.NotificationPreferenceDTO;
import org.clickenrent.contracts.notification.UpdatePreferencesRequest;
import org.clickenrent.notificationservice.service.NotificationPreferenceService;
import org.clickenrent.notificationservice.service.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for notification preference operations.
 */
@RestController
@RequestMapping("/api/notifications/preferences")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Preferences", description = "Manage notification preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;
    private final SecurityService securityService;

    /**
     * Get notification preferences for the current user.
     */
    @GetMapping
    @Operation(
            summary = "Get notification preferences",
            description = "Get notification preferences for the authenticated user",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<NotificationPreferenceDTO> getPreferences() {
        String userExternalId = securityService.getCurrentUserExternalId();
        log.info("Fetching preferences for user: {}", userExternalId);
        NotificationPreferenceDTO preferences = preferenceService.getPreferences(userExternalId);
        return ResponseEntity.ok(preferences);
    }

    /**
     * Update notification preferences for the current user.
     */
    @PutMapping
    @Operation(
            summary = "Update notification preferences",
            description = "Update notification preferences for the authenticated user",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<NotificationPreferenceDTO> updatePreferences(
            @Valid @RequestBody UpdatePreferencesRequest request
    ) {
        String userExternalId = securityService.getCurrentUserExternalId();
        log.info("Updating preferences for user: {}", userExternalId);
        NotificationPreferenceDTO preferences = preferenceService.updatePreferences(userExternalId, request);
        return ResponseEntity.ok(preferences);
    }
}

