package org.clickenrent.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.analyticsservice.client.AuthServiceClient;
import org.clickenrent.analyticsservice.dto.LanguageDTO;
import org.clickenrent.analyticsservice.dto.PeriodDTO;
import org.clickenrent.analyticsservice.dto.UserAnalyticsDTO;
import org.clickenrent.analyticsservice.dto.UserPageDTO;
import org.clickenrent.analyticsservice.dto.UserSimpleDTO;
import org.clickenrent.analyticsservice.dto.UserSummaryDTO;
import org.clickenrent.analyticsservice.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating user analytics.
 * Provides user registration, activity, and language distribution metrics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAnalyticsService {

    private final AuthServiceClient authServiceClient;
    private final SecurityService securityService;

    /**
     * Get user analytics for the specified period.
     *
     * @param from Start date (required)
     * @param to End date (required)
     * @param groupBy Grouping parameter (optional, future feature)
     * @return User analytics with summary and language breakdown
     */
    @Transactional(readOnly = true)
    public UserAnalyticsDTO getUserAnalytics(LocalDate from, LocalDate to, String groupBy) {
        // Check user permissions
        if (securityService.isCustomer()) {
            throw new UnauthorizedException("Customers don't have access to user analytics");
        }

        // Validate required parameters
        if (from == null || to == null) {
            throw new IllegalArgumentException("Start date (from) and end date (to) are required");
        }

        // Validate date range
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        log.info("Fetching user analytics for period: {} to {}", from, to);

        // Fetch all users from auth-service (with large page size to get all)
        UserPageDTO userPage = authServiceClient.getAllUsers(0, 1000);
        List<UserSimpleDTO> allUsers = userPage.getContent();

        // Filter users created within the date range
        List<UserSimpleDTO> usersInPeriod = allUsers.stream()
                .filter(user -> user.getDateCreated() != null)
                .filter(user -> {
                    LocalDate createdDate = user.getDateCreated().toLocalDate();
                    return !createdDate.isBefore(from) && !createdDate.isAfter(to);
                })
                .collect(Collectors.toList());

        // Build analytics response
        return UserAnalyticsDTO.builder()
                .period(PeriodDTO.builder()
                        .from(from)
                        .to(to)
                        .build())
                .summary(calculateSummary(usersInPeriod, allUsers))
                .languages(calculateLanguageBreakdown(allUsers))
                .build();
    }

    /**
     * Calculate user summary (total and active users).
     */
    private UserSummaryDTO calculateSummary(List<UserSimpleDTO> usersInPeriod, List<UserSimpleDTO> allUsers) {
        int totalUsers = usersInPeriod.size();
        int activeUsers = (int) allUsers.stream()
                .filter(user -> Boolean.TRUE.equals(user.getIsActive()))
                .count();

        return UserSummaryDTO.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .build();
    }

    /**
     * Calculate language distribution as percentages using language IDs.
     */
    private Map<String, Double> calculateLanguageBreakdown(List<UserSimpleDTO> users) {
        if (users == null || users.isEmpty()) {
            return new HashMap<>();
        }

        try {
            // Fetch all languages from auth-service
            List<LanguageDTO> languages = authServiceClient.getAllLanguages();
            
            // Create mapping: languageId -> language name
            Map<Long, String> languageIdToName = languages.stream()
                    .collect(Collectors.toMap(
                            LanguageDTO::getId,
                            LanguageDTO::getName,
                            (existing, replacement) -> existing // Keep first if duplicate
                    ));

            // Count users per language ID
            Map<Long, Long> languageCounts = users.stream()
                    .filter(user -> user.getLanguageId() != null)
                    .collect(Collectors.groupingBy(
                            UserSimpleDTO::getLanguageId,
                            Collectors.counting()
                    ));

            long totalUsersWithLanguage = languageCounts.values().stream()
                    .mapToLong(Long::longValue)
                    .sum();

            if (totalUsersWithLanguage == 0) {
                return new HashMap<>();
            }

            // Convert counts to percentages with language names as keys
            Map<String, Double> languagePercentages = new LinkedHashMap<>();
            
            // Sort by count descending
            languageCounts.entrySet().stream()
                    .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                    .forEach(entry -> {
                        Long languageId = entry.getKey();
                        Long count = entry.getValue();
                        
                        // Get language name from map (fallback to "Unknown" if not found)
                        String languageName = languageIdToName.getOrDefault(languageId, "Unknown (ID: " + languageId + ")");
                        
                        double percentage = (count * 100.0) / totalUsersWithLanguage;
                        languagePercentages.put(languageName, Math.round(percentage * 10.0) / 10.0);
                    });

            return languagePercentages;
            
        } catch (Exception e) {
            log.error("Error calculating language breakdown: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}
