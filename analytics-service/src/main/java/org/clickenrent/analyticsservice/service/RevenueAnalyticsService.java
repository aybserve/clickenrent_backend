package org.clickenrent.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.analyticsservice.client.PaymentServiceClient;
import org.clickenrent.analyticsservice.client.RentalServiceClient;
import org.clickenrent.analyticsservice.dto.*;
import org.clickenrent.analyticsservice.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating revenue analytics with earnings and refunds.
 * Provides revenue breakdown by location and handles multi-tenant data access.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RevenueAnalyticsService {

    private final RentalServiceClient rentalServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final SecurityService securityService;

    private static final String CURRENCY_EUR = "EUR";
    private static final int TOP_LOCATIONS_LIMIT = 5;
    private static final String REFUNDED_STATUS = "REFUNDED";
    private static final String PARTIALLY_REFUNDED_STATUS = "PARTIALLY_REFUNDED";

    /**
     * Get revenue analytics for the specified period.
     *
     * @param from Start date (required)
     * @param to End date (required)
     * @return Revenue analytics with summary and top locations
     */
    @Transactional(readOnly = true)
    public RevenueAnalyticsDTO getRevenueAnalytics(LocalDate from, LocalDate to) {
        // Check user permissions
        if (securityService.isCustomer()) {
            throw new UnauthorizedException("Customers don't have access to revenue analytics");
        }

        // Validate date range
        if (from == null || to == null) {
            throw new IllegalArgumentException("Start date (from) and end date (to) are required");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        log.info("Fetching revenue analytics for period: {} to {}", from, to);

        // Fetch bike rentals from rental-service
        BikeRentalPageDTO rentalPage = rentalServiceClient.getBikeRentals(0, 1000, from, to);
        List<BikeRentalSummaryDTO> rentals = rentalPage.getContent() != null ? 
                rentalPage.getContent() : Collections.emptyList();

        log.debug("Retrieved {} bike rentals for revenue calculation", rentals.size());

        // Calculate revenue metrics
        BigDecimal totalRevenue = calculateTotalRevenue(rentals);
        BigDecimal totalEarnings = calculateTotalEarnings(rentals);
        BigDecimal totalRefunds = calculateTotalRefunds(rentals);

        // Calculate top locations
        List<LocationRevenueDTO> topLocations = calculateTopLocations(rentals, totalRevenue);

        // Build response
        PeriodDTO period = PeriodDTO.builder()
                .from(from)
                .to(to)
                .build();

        RevenueSummaryDTO summary = RevenueSummaryDTO.builder()
                .totalRevenue(totalRevenue)
                .totalEarnings(totalEarnings)
                .totalRefunds(totalRefunds)
                .currency(CURRENCY_EUR)
                .build();

        return RevenueAnalyticsDTO.builder()
                .period(period)
                .summary(summary)
                .topLocations(topLocations)
                .build();
    }

    /**
     * Calculate total revenue from all bike rentals.
     * Total revenue = sum of totalPrice for all rentals.
     *
     * @param rentals List of bike rentals
     * @return Total revenue
     */
    private BigDecimal calculateTotalRevenue(List<BikeRentalSummaryDTO> rentals) {
        BigDecimal total = rentals.stream()
                .map(BikeRentalSummaryDTO::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("Calculated total revenue: {} EUR", total);
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate total earnings (revenue share) from all bike rentals.
     * Earnings = sum of (totalPrice * revenueSharePercent / 100) for all rentals.
     *
     * @param rentals List of bike rentals
     * @return Total earnings
     */
    private BigDecimal calculateTotalEarnings(List<BikeRentalSummaryDTO> rentals) {
        BigDecimal total = rentals.stream()
                .filter(rental -> rental.getTotalPrice() != null && rental.getRevenueSharePercent() != null)
                .map(rental -> {
                    BigDecimal totalPrice = rental.getTotalPrice();
                    BigDecimal sharePercent = rental.getRevenueSharePercent();
                    return totalPrice.multiply(sharePercent)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("Calculated total earnings: {} EUR", total);
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate total refunds from rental financial transactions.
     * Queries payment-service for all rental transactions and sums refunded amounts.
     *
     * @param rentals List of bike rentals
     * @return Total refunds
     */
    private BigDecimal calculateTotalRefunds(List<BikeRentalSummaryDTO> rentals) {
        if (rentals.isEmpty()) {
            log.debug("No rentals to check for refunds");
            return BigDecimal.ZERO;
        }

        try {
            // Extract rental external IDs to match with payment transactions
            Set<String> rentalExternalIds = rentals.stream()
                    .map(BikeRentalSummaryDTO::getRentalExternalId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            if (rentalExternalIds.isEmpty()) {
                log.warn("No rental external IDs found in bike rentals");
                return BigDecimal.ZERO;
            }

            log.debug("Checking refunds for {} unique rentals", rentalExternalIds.size());

            // Fetch rental financial transactions from payment-service
            RentalFinTransactionPageDTO transactionPage = paymentServiceClient.getRentalFinTransactions(0, 1000);
            List<RentalFinTransactionDTO> transactions = transactionPage.getContent() != null ?
                    transactionPage.getContent() : Collections.emptyList();

            log.debug("Retrieved {} rental financial transactions from payment-service", transactions.size());

            // Filter transactions matching our rentals and with REFUNDED status
            BigDecimal totalRefunds = transactions.stream()
                    .filter(transaction -> rentalExternalIds.contains(transaction.getRentalExternalId()))
                    .filter(transaction -> transaction.getFinancialTransaction() != null)
                    .filter(transaction -> {
                        FinancialTransactionSummaryDTO finTx = transaction.getFinancialTransaction();
                        return finTx.getPaymentStatus() != null &&
                               (REFUNDED_STATUS.equalsIgnoreCase(finTx.getPaymentStatus().getCode()) ||
                                PARTIALLY_REFUNDED_STATUS.equalsIgnoreCase(finTx.getPaymentStatus().getCode()));
                    })
                    .map(transaction -> transaction.getFinancialTransaction().getAmount())
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            log.debug("Calculated total refunds: {} EUR", totalRefunds);
            return totalRefunds.setScale(2, RoundingMode.HALF_UP);

        } catch (Exception e) {
            log.error("Error calculating refunds from payment-service: {}", e.getMessage(), e);
            // Return zero refunds on error rather than failing the entire request
            return BigDecimal.ZERO;
        }
    }

    /**
     * Calculate top locations by revenue.
     * Groups rentals by location, calculates revenue and earnings per location,
     * and returns top N locations by revenue.
     *
     * @param rentals List of bike rentals
     * @param totalRevenue Total revenue for percentage calculation
     * @return List of top locations (limited to TOP_LOCATIONS_LIMIT)
     */
    private List<LocationRevenueDTO> calculateTopLocations(List<BikeRentalSummaryDTO> rentals, 
                                                           BigDecimal totalRevenue) {
        if (rentals.isEmpty()) {
            log.debug("No rentals to calculate location breakdown");
            return Collections.emptyList();
        }

        // Group rentals by location
        Map<LocationKey, LocationMetrics> locationMetrics = new HashMap<>();

        for (BikeRentalSummaryDTO rental : rentals) {
            if (rental.getLocationId() == null) {
                continue;
            }

            LocationKey key = new LocationKey(rental.getLocationId(), rental.getLocationName());
            LocationMetrics metrics = locationMetrics.computeIfAbsent(key, k -> new LocationMetrics());

            // Add revenue
            if (rental.getTotalPrice() != null) {
                metrics.revenue = metrics.revenue.add(rental.getTotalPrice());
            }

            // Add earnings
            if (rental.getTotalPrice() != null && rental.getRevenueSharePercent() != null) {
                BigDecimal earnings = rental.getTotalPrice()
                        .multiply(rental.getRevenueSharePercent())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                metrics.earnings = metrics.earnings.add(earnings);
            }
        }

        log.debug("Calculated metrics for {} locations", locationMetrics.size());

        // Convert to DTOs and sort by revenue (descending)
        List<LocationRevenueDTO> locationList = locationMetrics.entrySet().stream()
                .map(entry -> {
                    LocationKey location = entry.getKey();
                    LocationMetrics metrics = entry.getValue();

                    // Calculate percentage of total revenue
                    Double percentage = 0.0;
                    if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                        percentage = metrics.revenue
                                .divide(totalRevenue, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                                .doubleValue();
                        // Round to 1 decimal place
                        percentage = Math.round(percentage * 10.0) / 10.0;
                    }

                    return LocationRevenueDTO.builder()
                            .locationId(location.id)
                            .name(location.name)
                            .revenue(metrics.revenue.setScale(2, RoundingMode.HALF_UP))
                            .earnings(metrics.earnings.setScale(2, RoundingMode.HALF_UP))
                            .percentage(percentage)
                            .build();
                })
                .sorted(Comparator.comparing(LocationRevenueDTO::getRevenue).reversed())
                .limit(TOP_LOCATIONS_LIMIT)
                .collect(Collectors.toList());

        log.debug("Returning top {} locations", locationList.size());
        return locationList;
    }

    /**
     * Internal class to hold location identifier (id + name).
     */
    private static class LocationKey {
        final Long id;
        final String name;

        LocationKey(Long id, String name) {
            this.id = id;
            this.name = name != null ? name : "Unknown Location";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocationKey that = (LocationKey) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    /**
     * Internal class to hold accumulated metrics per location.
     */
    private static class LocationMetrics {
        BigDecimal revenue = BigDecimal.ZERO;
        BigDecimal earnings = BigDecimal.ZERO;
    }
}
