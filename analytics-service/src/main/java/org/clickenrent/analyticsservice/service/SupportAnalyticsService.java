package org.clickenrent.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.analyticsservice.client.SupportServiceClient;
import org.clickenrent.analyticsservice.dto.PeriodDTO;
import org.clickenrent.analyticsservice.dto.SupportAnalyticsDTO;
import org.clickenrent.analyticsservice.dto.SupportRequestDTO;
import org.clickenrent.analyticsservice.dto.SupportSummaryDTO;
import org.clickenrent.analyticsservice.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for generating support ticket analytics.
 * Provides support request statistics by status and handles multi-tenant data access.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SupportAnalyticsService {

    private final SupportServiceClient supportServiceClient;
    private final SecurityService securityService;

    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_RESOLVED = "RESOLVED";

    /**
     * Get support analytics for the specified period.
     *
     * @param from Start date (required)
     * @param to End date (required)
     * @return Support analytics with summary statistics by status
     */
    @Transactional(readOnly = true)
    public SupportAnalyticsDTO getSupportAnalytics(LocalDate from, LocalDate to) {
        // Check user permissions
        if (securityService.isCustomer()) {
            throw new UnauthorizedException("Customers don't have access to support analytics");
        }

        // Validate date range
        if (from == null || to == null) {
            throw new IllegalArgumentException("Start date (from) and end date (to) are required");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        log.info("Fetching support analytics for period: {} to {}", from, to);

        // Fetch all support requests from support-service
        List<SupportRequestDTO> allRequests = supportServiceClient.getSupportRequests();
        
        log.debug("Retrieved {} support requests from support-service", 
                allRequests != null ? allRequests.size() : 0);

        // Filter by date range
        List<SupportRequestDTO> filteredRequests = filterByDateRange(
                allRequests != null ? allRequests : Collections.emptyList(), 
                from, 
                to
        );

        log.debug("Filtered to {} support requests within date range", filteredRequests.size());

        // Calculate summary
        SupportSummaryDTO summary = calculateSummary(filteredRequests);

        // Build response
        PeriodDTO period = PeriodDTO.builder()
                .from(from)
                .to(to)
                .build();

        return SupportAnalyticsDTO.builder()
                .period(period)
                .summary(summary)
                .build();
    }

    /**
     * Filter support requests by date range based on dateCreated field.
     *
     * @param requests List of support requests
     * @param from Start date (inclusive)
     * @param to End date (inclusive)
     * @return Filtered list of support requests
     */
    private List<SupportRequestDTO> filterByDateRange(List<SupportRequestDTO> requests, 
                                                      LocalDate from, 
                                                      LocalDate to) {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay(); // Exclusive end

        return requests.stream()
                .filter(request -> request.getDateCreated() != null)
                .filter(request -> !request.getDateCreated().isBefore(fromDateTime))
                .filter(request -> request.getDateCreated().isBefore(toDateTime))
                .collect(Collectors.toList());
    }

    /**
     * Calculate summary statistics from support requests.
     * Counts total requests and breaks down by status.
     *
     * @param requests List of filtered support requests
     * @return Summary with counts by status
     */
    private SupportSummaryDTO calculateSummary(List<SupportRequestDTO> requests) {
        int totalSupportRequests = requests.size();
        int openSupportRequests = 0;
        int inProgressSupportRequests = 0;
        int resolvedSupportRequests = 0;

        for (SupportRequestDTO request : requests) {
            String statusName = request.getSupportRequestStatusName();
            if (statusName == null) {
                continue;
            }

            if (STATUS_OPEN.equalsIgnoreCase(statusName)) {
                openSupportRequests++;
            } else if (STATUS_IN_PROGRESS.equalsIgnoreCase(statusName)) {
                inProgressSupportRequests++;
            } else if (STATUS_RESOLVED.equalsIgnoreCase(statusName)) {
                resolvedSupportRequests++;
            }
        }

        log.debug("Support summary - Total: {}, Open: {}, In Progress: {}, Resolved: {}", 
                totalSupportRequests, openSupportRequests, inProgressSupportRequests, resolvedSupportRequests);

        return SupportSummaryDTO.builder()
                .totalSupportRequests(totalSupportRequests)
                .openSupportRequests(openSupportRequests)
                .inProgressSupportRequests(inProgressSupportRequests)
                .resolvedSupportRequests(resolvedSupportRequests)
                .build();
    }
}
