package org.clickenrent.analyticsservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.clickenrent.analyticsservice.dto.UserAnalyticsDTO;
import org.clickenrent.analyticsservice.service.UserAnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller for user analytics endpoints.
 */
@RestController
@RequestMapping("/api/v1/analytics/users")
@RequiredArgsConstructor
@Tag(name = "User Analytics", description = "User registration, activity, and language distribution analytics")
@SecurityRequirement(name = "bearerAuth")
public class UserAnalyticsController {

    private final UserAnalyticsService userAnalyticsService;

    /**
     * Get user analytics including registration, activity, and language distribution.
     *
     * @param from Start date (required)
     * @param to End date (required)
     * @param groupBy Grouping parameter (optional, future feature)
     * @return User analytics data
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get user analytics",
            description = "Retrieve user analytics including total users, active users, and language distribution for the specified period. " +
                    "Admin and B2B users only."
    )
    public ResponseEntity<UserAnalyticsDTO> getUserAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String groupBy) {
        
        UserAnalyticsDTO analytics = userAnalyticsService.getUserAnalytics(from, to, groupBy);
        return ResponseEntity.ok(analytics);
    }
}
