package org.clickenrent.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.UpdateUserPreferenceRequest;
import org.clickenrent.authservice.dto.UserPreferenceDTO;
import org.clickenrent.authservice.service.UserPreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for User Preferences management.
 * 
 * Security Rules:
 * - SUPERADMIN/ADMIN: Can access and modify any user's preferences
 * - B2B/CUSTOMER: Can only access and modify their own preferences
 * - Cross-service endpoint (external ID) is public for service-to-service calls
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Preferences", description = "User preferences management endpoints for personalized UI/UX settings")
@SecurityRequirement(name = "bearerAuth")
public class UserPreferenceController {
    
    private final UserPreferenceService userPreferenceService;
    
    /**
     * Get user preferences by user ID.
     * Automatically creates default preferences if they don't exist.
     * 
     * GET /api/v1/users/{userId}/preferences
     */
    @GetMapping("/{userId}/preferences")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessUser(#userId)")
    @Operation(
            summary = "Get user preferences",
            description = "Returns user preferences by user ID. Automatically creates default preferences if they don't exist. " +
                          "Admins can view any user's preferences, regular users can only view their own."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User preferences retrieved successfully",
                    content = @Content(
                            schema = @Schema(implementation = UserPreferenceDTO.class),
                            examples = @ExampleObject(
                                    name = "User Preferences Example",
                                    value = """
                                    {
                                      "id": 1,
                                      "userId": 123,
                                      "userExternalId": "usr-ext-00001",
                                      "navigationOrder": {
                                        "superadmin": ["dashboard", "companies", "locations", "bikes"],
                                        "b2b": ["bikes", "bikeRentals", "analytics"]
                                      },
                                      "theme": "dark",
                                      "languageId": 1,
                                      "languageName": "English",
                                      "timezone": "America/New_York",
                                      "dateFormat": "MM/DD/YYYY",
                                      "timeFormat": "12h",
                                      "currencyExternalId": "550e8400-e29b-41d4-a716-446655440021",
                                      "emailNotifications": true,
                                      "pushNotifications": true,
                                      "smsNotifications": false,
                                      "notificationFrequency": "immediate",
                                      "itemsPerPage": 25,
                                      "dashboardLayout": {},
                                      "tablePreferences": {},
                                      "defaultFilters": {},
                                      "dateCreated": "2024-01-15T10:30:00",
                                      "lastDateModified": "2024-02-01T14:20:00"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserPreferenceDTO> getUserPreferences(
            @Parameter(description = "User ID", required = true, example = "123")
            @PathVariable Long userId) {
        UserPreferenceDTO preferences = userPreferenceService.getUserPreferences(userId);
        return ResponseEntity.ok(preferences);
    }
    
    /**
     * Get user preferences by external ID.
     * Public endpoint for cross-service communication.
     * 
     * GET /api/v1/users/external/{externalId}/preferences
     */
    @GetMapping("/external/{externalId}/preferences")
    @Operation(
            summary = "Get user preferences by external ID",
            description = "Returns user preferences by user external ID. Public endpoint for service-to-service communication."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User preferences retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserPreferenceDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserPreferenceDTO> getUserPreferencesByExternalId(
            @Parameter(description = "User external ID", required = true, example = "usr-ext-00001")
            @PathVariable String externalId) {
        UserPreferenceDTO preferences = userPreferenceService.getUserPreferencesByExternalId(externalId);
        return ResponseEntity.ok(preferences);
    }
    
    /**
     * Update user preferences.
     * Only provided fields will be updated (partial update).
     * 
     * PUT /api/v1/users/{userId}/preferences
     */
    @PutMapping("/{userId}/preferences")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessUser(#userId)")
    @Operation(
            summary = "Update user preferences",
            description = "Updates user preferences. Only provided fields will be updated (partial update). " +
                          "Admins can update any user's preferences, regular users can only update their own."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User preferences updated successfully",
                    content = @Content(
                            schema = @Schema(implementation = UserPreferenceDTO.class),
                            examples = @ExampleObject(
                                    name = "Update Preferences Example",
                                    value = """
                                    {
                                      "theme": "dark",
                                      "languageId": 4,
                                      "timezone": "Europe/Madrid",
                                      "navigationOrder": {
                                        "b2b": ["analytics", "bikes", "bikeRentals"]
                                      },
                                      "itemsPerPage": 50,
                                      "pushNotifications": false
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data - validation failed"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserPreferenceDTO> updateUserPreferences(
            @Parameter(description = "User ID", required = true, example = "123")
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserPreferenceRequest request) {
        UserPreferenceDTO updatedPreferences = userPreferenceService.updateUserPreferences(userId, request);
        return ResponseEntity.ok(updatedPreferences);
    }
    
    /**
     * Reset user preferences to default values.
     * 
     * POST /api/v1/users/{userId}/preferences/reset
     */
    @PostMapping("/{userId}/preferences/reset")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN') or @resourceSecurity.canAccessUser(#userId)")
    @Operation(
            summary = "Reset user preferences to defaults",
            description = "Resets user preferences to system default values. " +
                          "Admins can reset any user's preferences, regular users can only reset their own."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User preferences reset successfully",
                    content = @Content(schema = @Schema(implementation = UserPreferenceDTO.class))
            ),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserPreferenceDTO> resetUserPreferences(
            @Parameter(description = "User ID", required = true, example = "123")
            @PathVariable Long userId) {
        UserPreferenceDTO resetPreferences = userPreferenceService.resetToDefaults(userId);
        return ResponseEntity.ok(resetPreferences);
    }
}
