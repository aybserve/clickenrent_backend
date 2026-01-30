package org.clickenrent.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.analyticsservice.client.RentalServiceClient;
import org.clickenrent.analyticsservice.dto.*;
import org.clickenrent.analyticsservice.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating bike rental analytics.
 * Provides patterns, trends, peak times, and bike type breakdown.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BikeRentalAnalyticsService {

    private final RentalServiceClient rentalServiceClient;
    private final SecurityService securityService;

    private static final String UNIT_MINUTES = "minutes";

    /**
     * Get bike rental analytics for the specified period.
     *
     * @param from Start date (required)
     * @param to End date (required)
     * @param groupBy Grouping parameter (optional, future feature)
     * @return Bike rental analytics with patterns and trends
     */
    @Transactional(readOnly = true)
    public BikeRentalAnalyticsDTO getBikeRentalAnalytics(LocalDate from, LocalDate to, String groupBy) {
        // Check user permissions
        if (securityService.isCustomer()) {
            throw new UnauthorizedException("Customers don't have access to rental analytics");
        }

        // Validate required parameters
        if (from == null || to == null) {
            throw new IllegalArgumentException("Start date (from) and end date (to) are required");
        }

        // Validate date range
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        log.info("Fetching bike rental analytics for period: {} to {}", from, to);

        // Fetch bike rentals from rental-service
        BikeRentalPageDTO rentalPage = rentalServiceClient.getBikeRentals(0, 1000, from, to);
        List<BikeRentalSummaryDTO> rentals = rentalPage.getContent();

        // Build analytics response
        return BikeRentalAnalyticsDTO.builder()
                .period(PeriodDTO.builder()
                        .from(from)
                        .to(to)
                        .build())
                .summary(calculateSummary(rentals))
                .duration(calculateDuration(rentals))
                .peakHours(calculatePeakHours(rentals))
                .peakDays(calculatePeakDays(rentals))
                .bikeTypeBreakdown(calculateBikeTypeBreakdown(rentals))
                .build();
    }

    /**
     * Calculate summary statistics (total, completed, cancelled, cancellation rate).
     */
    private RentalSummaryDTO calculateSummary(List<BikeRentalSummaryDTO> rentals) {
        if (rentals == null || rentals.isEmpty()) {
            return RentalSummaryDTO.builder()
                    .totalBikeRentals(0)
                    .completedBikeRentals(0)
                    .cancelledBikeRentals(0)
                    .cancellationRate(0.0)
                    .build();
        }

        int total = rentals.size();
        int completed = (int) rentals.stream()
                .filter(r -> "COMPLETED".equalsIgnoreCase(r.getBikeRentalStatusName()))
                .count();
        int cancelled = (int) rentals.stream()
                .filter(r -> "CANCELLED".equalsIgnoreCase(r.getBikeRentalStatusName()))
                .count();

        double cancellationRate = total > 0 ? (cancelled * 100.0 / total) : 0.0;

        return RentalSummaryDTO.builder()
                .totalBikeRentals(total)
                .completedBikeRentals(completed)
                .cancelledBikeRentals(cancelled)
                .cancellationRate(Math.round(cancellationRate * 100.0) / 100.0)
                .build();
    }

    /**
     * Calculate duration statistics (min, max, average).
     */
    private RentalDurationDTO calculateDuration(List<BikeRentalSummaryDTO> rentals) {
        if (rentals == null || rentals.isEmpty()) {
            return RentalDurationDTO.builder()
                    .average(0)
                    .min(0)
                    .max(0)
                    .unit(UNIT_MINUTES)
                    .build();
        }

        List<Long> durations = new ArrayList<>();
        
        for (BikeRentalSummaryDTO rental : rentals) {
            long duration = calculateRentalDuration(rental.getExternalId());
            if (duration > 0) {
                durations.add(duration);
            }
        }

        if (durations.isEmpty()) {
            return RentalDurationDTO.builder()
                    .average(0)
                    .min(0)
                    .max(0)
                    .unit(UNIT_MINUTES)
                    .build();
        }

        long sum = durations.stream().mapToLong(Long::longValue).sum();
        long min = durations.stream().mapToLong(Long::longValue).min().orElse(0);
        long max = durations.stream().mapToLong(Long::longValue).max().orElse(0);
        long average = sum / durations.size();

        return RentalDurationDTO.builder()
                .average((int) average)
                .min((int) min)
                .max((int) max)
                .unit(UNIT_MINUTES)
                .build();
    }

    /**
     * Calculate peak hours (top 3 hours with most rentals).
     */
    private List<PeakHourDTO> calculatePeakHours(List<BikeRentalSummaryDTO> rentals) {
        if (rentals == null || rentals.isEmpty()) {
            return new ArrayList<>();
        }

        // Group by hour
        Map<Integer, Long> hourCounts = rentals.stream()
                .filter(r -> r.getStartDateTime() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getStartDateTime().getHour(),
                        Collectors.counting()
                ));

        // Sort by count descending and take top 3
        return hourCounts.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(3)
                .map(entry -> PeakHourDTO.builder()
                        .hour(entry.getKey())
                        .bikeRentals(entry.getValue().intValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Calculate peak days (top 2 days of week with most rentals).
     */
    private List<PeakDayDTO> calculatePeakDays(List<BikeRentalSummaryDTO> rentals) {
        if (rentals == null || rentals.isEmpty()) {
            return new ArrayList<>();
        }

        // Group by day of week
        Map<String, Long> dayCounts = rentals.stream()
                .filter(r -> r.getStartDateTime() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getStartDateTime().getDayOfWeek().name(),
                        Collectors.counting()
                ));

        // Sort by count descending and take top 2
        return dayCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(2)
                .map(entry -> PeakDayDTO.builder()
                        .dayOfWeek(entry.getKey())
                        .bikeRentals(entry.getValue().intValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Calculate bike type breakdown (count by type).
     */
    private List<BikeTypeBreakdownDTO> calculateBikeTypeBreakdown(List<BikeRentalSummaryDTO> rentals) {
        if (rentals == null || rentals.isEmpty()) {
            return new ArrayList<>();
        }

        // Group by bike type
        Map<String, Long> typeCounts = rentals.stream()
                .filter(r -> r.getBikeTypeName() != null && !r.getBikeTypeName().isEmpty())
                .collect(Collectors.groupingBy(
                        BikeRentalSummaryDTO::getBikeTypeName,
                        Collectors.counting()
                ));

        // Convert to DTOs and sort by count descending
        return typeCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> BikeTypeBreakdownDTO.builder()
                        .type(entry.getKey())
                        .count(entry.getValue().intValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Calculate total duration for a bike rental by summing all its ride durations.
     * 
     * @param bikeRentalExternalId External ID of the bike rental
     * @return Total duration in minutes
     */
    private long calculateRentalDuration(String bikeRentalExternalId) {
        try {
            // Fetch all rides for this bike rental
            List<RideSummaryDTO> rides = rentalServiceClient.getRidesByBikeRentalExternalId(bikeRentalExternalId);
            
            long totalDurationMinutes = 0;
            
            if (rides != null && !rides.isEmpty()) {
                for (RideSummaryDTO ride : rides) {
                    // Calculate duration for each ride
                    if (ride.getStartDateTime() != null && ride.getEndDateTime() != null) {
                        long rideMinutes = java.time.Duration.between(
                                ride.getStartDateTime(), 
                                ride.getEndDateTime()
                        ).toMinutes();
                        
                        totalDurationMinutes += rideMinutes;
                    }
                }
            }
            
            log.trace("Bike rental {} has total duration of {} minutes from {} rides", 
                    bikeRentalExternalId, totalDurationMinutes, rides != null ? rides.size() : 0);
            
            return totalDurationMinutes;
            
        } catch (Exception e) {
            log.warn("Error calculating duration for bike rental {}: {}", bikeRentalExternalId, e.getMessage());
            return 0;
        }
    }
}
