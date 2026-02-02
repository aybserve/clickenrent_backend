package org.clickenrent.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.analyticsservice.client.RentalServiceClient;
import org.clickenrent.analyticsservice.client.SupportServiceClient;
import org.clickenrent.analyticsservice.dto.*;
import org.clickenrent.analyticsservice.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Service for generating real-time analytics metrics.
 * Provides current system status for live dashboards.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RealtimeMetricsService {

    private final RentalServiceClient rentalServiceClient;
    private final SupportServiceClient supportServiceClient;
    private final SecurityService securityService;

    private static final String STATUS_ACTIVE = "Active";
    private static final Long STATUS_AVAILABLE = 1L;
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final int LAST_MINUTES_WINDOW = 15;

    /**
     * Get real-time metrics for live dashboards.
     * Returns current system status including active rentals, recent activity,
     * available bikes, and support requests.
     *
     * @return Real-time metrics DTO with current timestamp
     */
    @Transactional(readOnly = true)
    public RealtimeMetricsDTO getRealtimeMetrics() {
        // Check user permissions
        if (securityService.isCustomer()) {
            throw new UnauthorizedException("Customers don't have access to real-time analytics");
        }

        log.info("Fetching real-time metrics");

        // Get current timestamp
        String timestamp = Instant.now().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoffTime = now.minusMinutes(LAST_MINUTES_WINDOW);

        log.debug("Current time: {}, Cutoff time for recent activity: {}", now, cutoffTime);

        // Fetch all bike rentals from rental-service
        List<BikeRentalSummaryDTO> allRentals = fetchAllBikeRentals();
        log.debug("Retrieved {} bike rentals from rental-service", allRentals.size());

        // Fetch all bikes from rental-service
        List<BikeSummaryDTO> allBikes = fetchAllBikes();
        log.debug("Retrieved {} bikes from rental-service", allBikes.size());

        // Fetch all support requests from support-service
        List<SupportRequestDTO> allSupportRequests = fetchAllSupportRequests();
        log.debug("Retrieved {} support requests from support-service", allSupportRequests.size());

        // Calculate metrics
        Integer activeBikeRentals = calculateActiveBikeRentals(allRentals);
        Integer bikeRentalsLast15Minutes = calculateRecentRentals(allRentals, cutoffTime);
        BigDecimal revenueLast15Minutes = calculateRecentRevenue(allRentals, cutoffTime);
        Integer availableBikes = calculateAvailableBikes(allBikes);
        Integer inProgressSupportRequests = calculateInProgressSupport(allSupportRequests);

        log.debug("Real-time metrics - Active rentals: {}, Recent rentals: {}, Recent revenue: {}, Available bikes: {}, In-progress support: {}",
                activeBikeRentals, bikeRentalsLast15Minutes, revenueLast15Minutes, availableBikes, inProgressSupportRequests);

        return RealtimeMetricsDTO.builder()
                .timestamp(timestamp)
                .activeBikeRentals(activeBikeRentals)
                .bikeRentalsLast15Minutes(bikeRentalsLast15Minutes)
                .revenueLast15Minutes(revenueLast15Minutes)
                .availableBikes(availableBikes)
                .inProgressSupportRequests(inProgressSupportRequests)
                .build();
    }

    /**
     * Fetch all bike rentals from rental-service.
     * Handles pagination if needed.
     *
     * @return List of all bike rentals
     */
    private List<BikeRentalSummaryDTO> fetchAllBikeRentals() {
        try {
            BikeRentalPageDTO page = rentalServiceClient.getBikeRentals(0, 1000, null, null);
            return page != null && page.getContent() != null ? page.getContent() : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching bike rentals from rental-service", e);
            return Collections.emptyList();
        }
    }

    /**
     * Fetch all bikes from rental-service.
     * Handles pagination if needed.
     *
     * @return List of all bikes
     */
    private List<BikeSummaryDTO> fetchAllBikes() {
        try {
            BikePageDTO page = rentalServiceClient.getBikes(0, 1000);
            return page != null && page.getContent() != null ? page.getContent() : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching bikes from rental-service", e);
            return Collections.emptyList();
        }
    }

    /**
     * Fetch all support requests from support-service.
     *
     * @return List of all support requests
     */
    private List<SupportRequestDTO> fetchAllSupportRequests() {
        try {
            List<SupportRequestDTO> requests = supportServiceClient.getSupportRequests();
            return requests != null ? requests : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching support requests from support-service", e);
            return Collections.emptyList();
        }
    }

    /**
     * Calculate count of active bike rentals.
     *
     * @param rentals List of all bike rentals
     * @return Count of rentals with status "Active"
     */
    private Integer calculateActiveBikeRentals(List<BikeRentalSummaryDTO> rentals) {
        int count = (int) rentals.stream()
                .filter(rental -> rental.getBikeRentalStatusName() != null)
                .filter(rental -> STATUS_ACTIVE.equalsIgnoreCase(rental.getBikeRentalStatusName()))
                .count();

        log.debug("Active bike rentals: {}", count);
        return count;
    }

    /**
     * Calculate count of bike rentals started in the last 15 minutes.
     *
     * @param rentals List of all bike rentals
     * @param cutoffTime Cutoff time for recent activity (NOW - 15 minutes)
     * @return Count of rentals started after cutoff time
     */
    private Integer calculateRecentRentals(List<BikeRentalSummaryDTO> rentals, LocalDateTime cutoffTime) {
        int count = (int) rentals.stream()
                .filter(rental -> rental.getStartDateTime() != null)
                .filter(rental -> !rental.getStartDateTime().isBefore(cutoffTime))
                .count();

        log.debug("Bike rentals in last {} minutes: {}", LAST_MINUTES_WINDOW, count);
        return count;
    }

    /**
     * Calculate total revenue from bike rentals started in the last 15 minutes.
     *
     * @param rentals List of all bike rentals
     * @param cutoffTime Cutoff time for recent activity (NOW - 15 minutes)
     * @return Sum of totalPrice for rentals started after cutoff time
     */
    private BigDecimal calculateRecentRevenue(List<BikeRentalSummaryDTO> rentals, LocalDateTime cutoffTime) {
        BigDecimal revenue = rentals.stream()
                .filter(rental -> rental.getStartDateTime() != null)
                .filter(rental -> !rental.getStartDateTime().isBefore(cutoffTime))
                .map(BikeRentalSummaryDTO::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        log.debug("Revenue in last {} minutes: {}", LAST_MINUTES_WINDOW, revenue);
        return revenue;
    }

    /**
     * Calculate count of available bikes.
     *
     * @param bikes List of all bikes
     * @return Count of bikes with status AVAILABLE (bikeStatusId = 1)
     */
    private Integer calculateAvailableBikes(List<BikeSummaryDTO> bikes) {
        int count = (int) bikes.stream()
                .filter(bike -> bike.getBikeStatusId() != null)
                .filter(bike -> STATUS_AVAILABLE.equals(bike.getBikeStatusId()))
                .count();

        log.debug("Available bikes: {}", count);
        return count;
    }

    /**
     * Calculate count of support requests with status IN_PROGRESS.
     *
     * @param supportRequests List of all support requests
     * @return Count of support requests with status "IN_PROGRESS"
     */
    private Integer calculateInProgressSupport(List<SupportRequestDTO> supportRequests) {
        int count = (int) supportRequests.stream()
                .filter(request -> request.getSupportRequestStatusName() != null)
                .filter(request -> STATUS_IN_PROGRESS.equalsIgnoreCase(request.getSupportRequestStatusName()))
                .count();

        log.debug("In-progress support requests: {}", count);
        return count;
    }
}
