package org.clickenrent.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.analyticsservice.client.RentalServiceClient;
import org.clickenrent.analyticsservice.dto.LocationAnalyticsDTO;
import org.clickenrent.analyticsservice.dto.LocationDTO;
import org.clickenrent.analyticsservice.dto.LocationPageDTO;
import org.clickenrent.analyticsservice.dto.LocationSummaryDTO;
import org.clickenrent.analyticsservice.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Service for generating location analytics including summary statistics.
 * Provides location overview and handles multi-tenant data access.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LocationAnalyticsService {

    private final RentalServiceClient rentalServiceClient;
    private final SecurityService securityService;

    /**
     * Get location analytics including summary and full list of locations.
     *
     * @return Location analytics with summary and locations list
     */
    @Transactional(readOnly = true)
    public LocationAnalyticsDTO getLocationAnalytics() {
        // Check user permissions
        if (securityService.isCustomer()) {
            throw new UnauthorizedException("Customers don't have access to location analytics");
        }

        log.info("Fetching location analytics for company");

        // Fetch all locations from rental-service
        LocationPageDTO locationPage = rentalServiceClient.getLocations(0, 1000);
        List<LocationDTO> locations = locationPage.getContent() != null ? 
                locationPage.getContent() : Collections.emptyList();

        log.debug("Retrieved {} locations for analytics", locations.size());

        // Calculate summary
        LocationSummaryDTO summary = calculateSummary(locations);

        return LocationAnalyticsDTO.builder()
                .summary(summary)
                .locations(locations)
                .build();
    }

    /**
     * Calculate summary statistics from locations.
     *
     * @param locations List of locations
     * @return Summary with total, active, and inactive counts
     */
    private LocationSummaryDTO calculateSummary(List<LocationDTO> locations) {
        int totalLocations = locations.size();
        int activeLocations = 0;
        int inactiveLocations = 0;

        for (LocationDTO location : locations) {
            Boolean isActive = location.getIsActive();
            if (isActive != null && isActive) {
                activeLocations++;
            } else {
                inactiveLocations++;
            }
        }

        log.debug("Location summary - Total: {}, Active: {}, Inactive: {}", 
                totalLocations, activeLocations, inactiveLocations);

        return LocationSummaryDTO.builder()
                .totalLocations(totalLocations)
                .activeLocations(activeLocations)
                .inactiveLocations(inactiveLocations)
                .build();
    }
}
