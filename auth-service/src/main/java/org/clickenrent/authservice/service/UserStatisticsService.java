package org.clickenrent.authservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.authservice.client.RentalServiceClient;
import org.clickenrent.authservice.client.SupportServiceClient;
import org.clickenrent.authservice.dto.FavoriteLocationDTO;
import org.clickenrent.authservice.dto.UserStatsDTO;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.repository.UserRepository;
import org.clickenrent.contracts.rental.BikeRentalDTO;
import org.clickenrent.contracts.rental.RentalDTO;
import org.clickenrent.contracts.rental.RideDTO;
import org.clickenrent.contracts.support.BikeRentalFeedbackDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for calculating user bike rental statistics.
 * Aggregates data from rental-service and support-service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserStatisticsService {

    private final UserRepository userRepository;
    private final RentalServiceClient rentalServiceClient;
    private final SupportServiceClient supportServiceClient;

    /**
     * Calculate comprehensive statistics for a user.
     * Uses graceful degradation - returns partial data if services are unavailable.
     */
    public UserStatsDTO getUserStats(Long userId) {
        // Get user and extract external ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        String userExternalId = user.getExternalId();
        
        log.info("=== Starting statistics calculation for user: {} (externalId: {}) ===", userId, userExternalId);

        // Initialize stats with default values
        UserStatsDTO stats = UserStatsDTO.builder()
                .totalBikeRentals(0)
                .totalRidesDurationTime(0L)
                .totalSpent(BigDecimal.ZERO)
                .averageRating(null)
                .favoriteLocation(null)
                .build();

        // Fetch and calculate rental statistics
        try {
            log.debug("Attempting to fetch rental statistics from rental-service...");
            calculateRentalStats(userExternalId, stats);
            log.info("Successfully calculated rental statistics: totalBikeRentals={}, totalSpent={}, totalRidesDuration={}", 
                    stats.getTotalBikeRentals(), stats.getTotalSpent(), stats.getTotalRidesDurationTime());
        } catch (FeignException e) {
            log.error("Feign error fetching rental data for user {}: Status={}, Message={}", 
                    userExternalId, e.status(), e.getMessage());
            log.error("Response body: {}", e.contentUTF8());
            // Continue with default values for rental stats
        } catch (Exception e) {
            log.error("Unexpected error fetching rental data for user {}: {}", userExternalId, e.getMessage(), e);
        }

        // Fetch and calculate feedback statistics
        try {
            log.debug("Attempting to fetch feedback statistics from support-service...");
            calculateFeedbackStats(userExternalId, stats);
            log.info("Successfully calculated feedback statistics: averageRating={}", stats.getAverageRating());
        } catch (FeignException e) {
            log.error("Feign error fetching feedback data for user {}: Status={}, Message={}", 
                    userExternalId, e.status(), e.getMessage());
            log.error("Response body: {}", e.contentUTF8());
            // Continue with default values for feedback stats
        } catch (Exception e) {
            log.error("Unexpected error fetching feedback data for user {}: {}", userExternalId, e.getMessage(), e);
        }

        log.info("=== Completed statistics calculation for user: {} ===", userId);
        return stats;
    }

    /**
     * Calculate statistics from rental-service data.
     */
    private void calculateRentalStats(String userExternalId, UserStatsDTO stats) {
        // Get all rentals for the user
        log.debug("Calling rental-service: GET /api/rentals/user/{}", userExternalId);
        List<RentalDTO> rentals = rentalServiceClient.getRentalsByUserExternalId(userExternalId);
        
        if (rentals == null || rentals.isEmpty()) {
            log.info("No rentals found for user: {}", userExternalId);
            return;
        }
        
        log.debug("Found {} rentals for user: {}", rentals.size(), userExternalId);

        // Collect all bike rentals across all rentals
        List<BikeRentalDTO> allBikeRentals = new ArrayList<>();
        for (RentalDTO rental : rentals) {
            try {
                log.debug("Fetching bike rentals for rental externalId: {}", rental.getExternalId());
                List<BikeRentalDTO> bikeRentals = rentalServiceClient.getBikeRentalsByRentalExternalId(rental.getExternalId());
                if (bikeRentals != null) {
                    log.debug("Found {} bike rentals for rental: {}", bikeRentals.size(), rental.getExternalId());
                    allBikeRentals.addAll(bikeRentals);
                }
            } catch (Exception e) {
                log.warn("Failed to fetch bike rentals for rental {}: {}", rental.getExternalId(), e.getMessage());
            }
        }

        // Calculate total bike rentals
        stats.setTotalBikeRentals(allBikeRentals.size());

        // Calculate total spent (sum of all totalPrice values)
        BigDecimal totalSpent = allBikeRentals.stream()
                .map(BikeRentalDTO::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalSpent(totalSpent);

        // Calculate favorite location
        FavoriteLocationDTO favoriteLocation = calculateFavoriteLocation(allBikeRentals);
        stats.setFavoriteLocation(favoriteLocation);

        // Calculate total rides duration
        Long totalDuration = calculateTotalRidesDuration(allBikeRentals);
        stats.setTotalRidesDurationTime(totalDuration);
    }

    /**
     * Calculate total duration of all rides in minutes.
     */
    private Long calculateTotalRidesDuration(List<BikeRentalDTO> bikeRentals) {
        long totalMinutes = 0;

        for (BikeRentalDTO bikeRental : bikeRentals) {
            try {
                log.debug("Fetching rides for bike rental externalId: {}", bikeRental.getExternalId());
                List<RideDTO> rides = rentalServiceClient.getRidesByBikeRentalExternalId(bikeRental.getExternalId());
                
                if (rides != null) {
                    log.debug("Found {} rides for bike rental: {}", rides.size(), bikeRental.getExternalId());
                    for (RideDTO ride : rides) {
                        if (ride.getStartDateTime() != null && ride.getEndDateTime() != null) {
                            Duration duration = Duration.between(ride.getStartDateTime(), ride.getEndDateTime());
                            long minutes = duration.toMinutes();
                            log.debug("Ride duration: {} minutes", minutes);
                            totalMinutes += minutes;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to fetch rides for bike rental {}: {}", bikeRental.getExternalId(), e.getMessage());
            }
        }

        log.debug("Total rides duration: {} minutes", totalMinutes);
        return totalMinutes;
    }

    /**
     * Find the most frequently used location.
     */
    private FavoriteLocationDTO calculateFavoriteLocation(List<BikeRentalDTO> bikeRentals) {
        if (bikeRentals.isEmpty()) {
            return null;
        }

        // Count occurrences of each location by external ID
        Map<String, Long> locationCounts = bikeRentals.stream()
                .map(BikeRentalDTO::getLocationExternalId)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        locationExternalId -> locationExternalId,
                        Collectors.counting()
                ));

        if (locationCounts.isEmpty()) {
            return null;
        }

        // Find the location with the maximum count
        Map.Entry<String, Long> maxEntry = locationCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (maxEntry == null) {
            return null;
        }

        String favoriteLocationExternalId = maxEntry.getKey();
        Integer timesUsed = maxEntry.getValue().intValue();

        // Get location name from the first bike rental with this location
        String locationName = bikeRentals.stream()
                .filter(br -> favoriteLocationExternalId.equals(br.getLocationExternalId()))
                .findFirst()
                .map(br -> {
                    // Note: BikeRentalDTO doesn't have location name directly
                    // We would need to fetch it from rental-service or include it in the DTO
                    // For now, we'll use a placeholder or the externalId
                    return "Location"; // Placeholder - ideally fetch from location service
                })
                .orElse("Unknown Location");

        return FavoriteLocationDTO.builder()
                .externalId(favoriteLocationExternalId)
                .name(locationName)
                .timesUsed(timesUsed)
                .build();
    }

    /**
     * Calculate statistics from support-service data.
     */
    private void calculateFeedbackStats(String userExternalId, UserStatsDTO stats) {
        log.debug("Calling support-service: GET /api/bike-rental-feedbacks/user/{}", userExternalId);
        List<BikeRentalFeedbackDTO> feedbacks = supportServiceClient.getByUserExternalId(userExternalId);
        
        if (feedbacks == null || feedbacks.isEmpty()) {
            log.info("No feedback found for user: {}", userExternalId);
            return;
        }
        
        log.debug("Found {} feedbacks for user: {}", feedbacks.size(), userExternalId);

        // Calculate average rating
        Double averageRating = feedbacks.stream()
                .map(BikeRentalFeedbackDTO::getRate)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        // Round to 1 decimal place
        averageRating = BigDecimal.valueOf(averageRating)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();

        stats.setAverageRating(averageRating > 0 ? averageRating : null);
    }
}

