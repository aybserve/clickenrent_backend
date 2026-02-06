package org.clickenrent.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.analyticsservice.client.RentalServiceClient;
import org.clickenrent.analyticsservice.dto.*;
import org.clickenrent.analyticsservice.entity.AnalyticsDailySummary;
import org.clickenrent.analyticsservice.exception.UnauthorizedException;
import org.clickenrent.analyticsservice.repository.AnalyticsDailySummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Service for generating dashboard overview with aggregated KPIs.
 * Provides period-over-period comparison and handles multi-tenant data access.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final AnalyticsDailySummaryRepository repository;
    private final SecurityService securityService;
    private final RentalServiceClient rentalServiceClient;

    private static final int DEFAULT_PERIOD_DAYS = 30;
    private static final String CURRENCY_EUR = "EUR";
    private static final String UNIT_MINUTES = "minutes";
    private static final String UNIT_PERCENT = "percent";
    private static final String DIRECTION_UP = "up";
    private static final String DIRECTION_DOWN = "down";

    /**
     * Get dashboard overview with aggregated KPIs and period comparison.
     *
     * @param from Start date (default: 30 days ago)
     * @param to End date (default: today)
     * @param compareWithPrevious Whether to include comparison with previous period
     * @return Dashboard overview with KPIs
     */
    @Transactional(readOnly = true)
    public DashboardOverviewDTO getDashboardOverview(LocalDate from, LocalDate to, Boolean compareWithPrevious) {
        // Check user permissions
        if (securityService.isCustomer()) {
            throw new UnauthorizedException("Customers don't have access to dashboard analytics");
        }

        // Calculate period dates with defaults
        LocalDate currentFrom = from != null ? from : LocalDate.now().minusDays(DEFAULT_PERIOD_DAYS);
        LocalDate currentTo = to != null ? to : LocalDate.now();

        // Validate date range
        if (currentFrom.isAfter(currentTo)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        log.info("Fetching dashboard overview for period: {} to {}, compareWithPrevious: {}", 
                currentFrom, currentTo, compareWithPrevious);

        // Query real-time bike rental data from rental-service
        RealTimeMetrics currentMetrics = queryRealTimeMetrics(currentFrom, currentTo);

        // Query previous period data if comparison is enabled
        RealTimeMetrics previousMetrics = null;
        if (Boolean.TRUE.equals(compareWithPrevious)) {
            long periodDuration = java.time.temporal.ChronoUnit.DAYS.between(currentFrom, currentTo);
            LocalDate previousFrom = currentFrom.minusDays(periodDuration + 1);
            LocalDate previousTo = currentFrom.minusDays(1);
            previousMetrics = queryRealTimeMetrics(previousFrom, previousTo);
            log.debug("Previous period: {} to {}", previousFrom, previousTo);
        }

        // Build and return response
        return buildDashboardOverviewFromRealTime(currentFrom, currentTo, currentMetrics, previousMetrics);
    }

    /**
     * Query real-time metrics from rental-service for the specified period.
     * The rental-service automatically filters bike rentals by user's company.
     */
    private RealTimeMetrics queryRealTimeMetrics(LocalDate from, LocalDate to) {
        try {
            // Call rental-service to get bike rentals for the period
            // Rental-service will apply company filtering based on JWT token
            BikeRentalPageDTO rentalPage = rentalServiceClient.getBikeRentals(0, 1000, from, to);
            
            RealTimeMetrics metrics = new RealTimeMetrics();
            metrics.totalBikeRentals = (int) rentalPage.getTotalElements();
            
            long totalDurationMinutesAllRentals = 0;
            
            // Calculate metrics from the rental data
            if (rentalPage.getContent() != null && !rentalPage.getContent().isEmpty()) {
                for (BikeRentalSummaryDTO rental : rentalPage.getContent()) {
                    // Sum revenue
                    if (rental.getTotalPrice() != null) {
                        metrics.totalRevenueCents += rental.getTotalPrice()
                                .multiply(BigDecimal.valueOf(100))
                                .longValue();
                    }
                    
                    // Calculate duration for this bike rental from all its rides
                    long bikeRentalTotalDuration = calculateBikeRentalDuration(rental.getExternalId());
                    totalDurationMinutesAllRentals += bikeRentalTotalDuration;
                    
                    // Count completed vs cancelled
                    if (rental.getBikeRentalStatusName() != null) {
                        if (rental.getBikeRentalStatusName().equalsIgnoreCase("COMPLETED")) {
                            metrics.completedBikeRentals++;
                        } else if (rental.getBikeRentalStatusName().equalsIgnoreCase("CANCELLED")) {
                            metrics.cancelledBikeRentals++;
                        }
                    }
                }
                
                // Calculate average duration across all bike rentals
                // Average = (sum of all bike rental durations) / count of bike rentals
                if (metrics.totalBikeRentals > 0) {
                    metrics.averageBikeRentalDurationMinutes = 
                            (int) (totalDurationMinutesAllRentals / metrics.totalBikeRentals);
                }
                
                metrics.totalBikeRentalDurationMinutes = totalDurationMinutesAllRentals;
            }
            
            log.debug("Queried {} bike rentals from rental-service for period {} to {} (total duration: {} min, avg: {} min)", 
                    metrics.totalBikeRentals, from, to, 
                    metrics.totalBikeRentalDurationMinutes, metrics.averageBikeRentalDurationMinutes);
            
            return metrics;
            
        } catch (Exception e) {
            log.error("Error querying rental-service for period {} to {}: {}", from, to, e.getMessage());
            // Return empty metrics on error
            return new RealTimeMetrics();
        }
    }

    /**
     * Calculate total duration for a bike rental by summing all its ride durations.
     * 
     * @param bikeRentalExternalId External ID of the bike rental
     * @return Total duration in minutes
     */
    private long calculateBikeRentalDuration(String bikeRentalExternalId) {
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

    /**
     * Build dashboard overview from real-time metrics.
     */
    private DashboardOverviewDTO buildDashboardOverviewFromRealTime(
            LocalDate from, LocalDate to,
            RealTimeMetrics currentMetrics,
            RealTimeMetrics previousMetrics) {

        // Build KPIs with comparisons
        DashboardKPIsDTO kpis = buildRealTimeKPIs(currentMetrics, previousMetrics);

        // Build period info
        PeriodDTO period = PeriodDTO.builder()
                .from(from)
                .to(to)
                .build();

        return DashboardOverviewDTO.builder()
                .period(period)
                .kpis(kpis)
                .generatedAt(ZonedDateTime.now())
                .build();
    }

    /**
     * Build KPIs from real-time metrics.
     */
    private DashboardKPIsDTO buildRealTimeKPIs(RealTimeMetrics current, RealTimeMetrics previous) {
        return DashboardKPIsDTO.builder()
                .totalBikeRentals(buildMetricKpi(
                        current.totalBikeRentals,
                        previous != null ? previous.totalBikeRentals : null))
                .totalRevenue(buildRevenueKpi(
                        current.totalRevenueCents,
                        previous != null ? previous.totalRevenueCents : null))
                .activeCustomers(buildMetricKpi(
                        current.activeCustomers,
                        previous != null ? previous.activeCustomers : null))
                .newRegistrations(buildMetricKpi(
                        current.newRegistrations,
                        previous != null ? previous.newRegistrations : null))
                .averageBikeRentalDuration(buildDurationKpi(
                        current.averageBikeRentalDurationMinutes,
                        previous != null ? previous.averageBikeRentalDurationMinutes : null))
                .build();
    }

    /**
     * Query analytics data for a specific period with security filtering.
     */
    private List<AnalyticsDailySummary> queryPeriodData(LocalDate from, LocalDate to) {
        if (securityService.isAdmin()) {
            // Admin can see all companies
            return repository.findBySummaryDateBetween(from, to);
        } else if (securityService.isB2B()) {
            // B2B users see their companies (Hibernate filter applies automatically)
            List<String> companyExternalIds = securityService.getCurrentUserCompanyExternalIds();
            if (companyExternalIds.isEmpty()) {
                log.warn("B2B user has no associated companies");
                return List.of();
            }
            // For B2B, we need to query each company and combine results
            // However, Hibernate filter should handle this automatically
            return repository.findBySummaryDateBetween(from, to);
        }
        
        return List.of();
    }

    /**
     * Build dashboard overview DTO from aggregated data.
     */
    private DashboardOverviewDTO buildDashboardOverview(
            LocalDate from, LocalDate to,
            List<AnalyticsDailySummary> currentData,
            List<AnalyticsDailySummary> previousData) {

        // Aggregate metrics for current and previous periods
        AggregatedMetrics currentMetrics = aggregateMetrics(currentData);
        AggregatedMetrics previousMetrics = previousData != null ? aggregateMetrics(previousData) : null;

        // Build KPIs with comparisons
        DashboardKPIsDTO kpis = buildKPIs(currentMetrics, previousMetrics);

        // Build period info
        PeriodDTO period = PeriodDTO.builder()
                .from(from)
                .to(to)
                .build();

        return DashboardOverviewDTO.builder()
                .period(period)
                .kpis(kpis)
                .generatedAt(ZonedDateTime.now())
                .build();
    }

    /**
     * Aggregate metrics from daily summaries.
     * Different metrics require different aggregation strategies:
     * - Cumulative counts (rentals, registrations): SUM across days
     * - Active/snapshot metrics (active customers, fleet): Use LATEST or MAX
     * - Revenue: SUM across days
     */
    private AggregatedMetrics aggregateMetrics(List<AnalyticsDailySummary> summaries) {
        if (summaries == null || summaries.isEmpty()) {
            return new AggregatedMetrics();
        }

        AggregatedMetrics metrics = new AggregatedMetrics();
        int maxActiveCustomers = 0;

        // Aggregate metrics from all daily summaries
        for (AnalyticsDailySummary summary : summaries) {
            // SUM: Count of bike rentals across the period
            metrics.totalBikeRentals += summary.getTotalBikeRentals();
            
            // SUM: Total revenue across the period
            metrics.totalRevenueCents += summary.getTotalRevenueCents();
            
            // MAX: Active customers is a snapshot, take the maximum daily value
            maxActiveCustomers = Math.max(maxActiveCustomers, summary.getActiveCustomers());
            
            // SUM: New registrations across the period
            metrics.newRegistrations += summary.getNewCustomers();
            
            // SUM: Total duration to calculate average
            metrics.totalBikeRentalDurationMinutes += summary.getTotalBikeRentalDurationMinutes();
            
            // LATEST: Fleet metrics are snapshots, use the latest (last in list)
            metrics.totalBikes = summary.getTotalBikes();
            metrics.inUseBikes = summary.getInUseBikes();
        }

        // Set the active customers to the maximum observed
        metrics.activeCustomers = maxActiveCustomers;

        // Calculate average bike rental duration across the entire period
        if (metrics.totalBikeRentals > 0 && metrics.totalBikeRentalDurationMinutes > 0) {
            metrics.averageBikeRentalDurationMinutes = 
                    (int) (metrics.totalBikeRentalDurationMinutes / metrics.totalBikeRentals);
        }

        return metrics;
    }

    /**
     * Build KPIs with comparison data.
     */
    private DashboardKPIsDTO buildKPIs(AggregatedMetrics current, AggregatedMetrics previous) {
        return DashboardKPIsDTO.builder()
                .totalBikeRentals(buildMetricKpi(
                        current.totalBikeRentals,
                        previous != null ? previous.totalBikeRentals : null))
                .totalRevenue(buildRevenueKpi(
                        current.totalRevenueCents,
                        previous != null ? previous.totalRevenueCents : null))
                .activeCustomers(buildMetricKpi(
                        current.activeCustomers,
                        previous != null ? previous.activeCustomers : null))
                .newRegistrations(buildMetricKpi(
                        current.newRegistrations,
                        previous != null ? previous.newRegistrations : null))
                .averageBikeRentalDuration(buildDurationKpi(
                        current.averageBikeRentalDurationMinutes,
                        previous != null ? previous.averageBikeRentalDurationMinutes : null))
                .build();
    }

    /**
     * Build metric KPI with change calculation.
     */
    private KpiMetricDTO buildMetricKpi(Integer currentValue, Integer previousValue) {
        KpiMetricDTO.KpiMetricDTOBuilder builder = KpiMetricDTO.builder()
                .value(currentValue);

        if (previousValue != null && previousValue > 0) {
            Double change = calculateChange(currentValue, previousValue);
            builder.change(change)
                   .changeDirection(determineChangeDirection(change))
                   .previousValue(previousValue);
        }

        return builder.build();
    }

    /**
     * Build revenue KPI with currency and change calculation.
     */
    private KpiRevenueDTO buildRevenueKpi(Long currentCents, Long previousCents) {
        BigDecimal currentValue = convertCentsToEuros(currentCents);
        
        KpiRevenueDTO.KpiRevenueDTOBuilder builder = KpiRevenueDTO.builder()
                .value(currentValue)
                .currency(CURRENCY_EUR);

        if (previousCents != null && previousCents > 0) {
            BigDecimal previousValue = convertCentsToEuros(previousCents);
            Double change = calculateChange(currentCents, previousCents);
            builder.change(change)
                   .changeDirection(determineChangeDirection(change))
                   .previousValue(previousValue);
        }

        return builder.build();
    }

    /**
     * Build duration KPI with change calculation.
     */
    private KpiDurationDTO buildDurationKpi(Integer currentValue, Integer previousValue) {
        KpiDurationDTO.KpiDurationDTOBuilder builder = KpiDurationDTO.builder()
                .value(currentValue)
                .unit(UNIT_MINUTES);

        if (previousValue != null && previousValue > 0) {
            Double change = calculateChange(currentValue, previousValue);
            builder.change(change)
                   .changeDirection(determineChangeDirection(change));
        }

        return builder.build();
    }

    /**
     * Build percentage KPI with change calculation.
     */
    private KpiPercentageDTO buildPercentageKpi(Double currentValue, Double previousValue) {
        KpiPercentageDTO.KpiPercentageDTOBuilder builder = KpiPercentageDTO.builder()
                .value(currentValue)
                .unit(UNIT_PERCENT);

        if (previousValue != null && previousValue > 0) {
            // For percentage values, calculate absolute change (not percentage of percentage)
            Double change = currentValue - previousValue;
            builder.change(change)
                   .changeDirection(determineChangeDirection(change));
        }

        return builder.build();
    }

    /**
     * Calculate percentage change between current and previous values.
     */
    private Double calculateChange(Number current, Number previous) {
        if (previous == null || previous.doubleValue() == 0) {
            return null;
        }
        
        double currentVal = current.doubleValue();
        double previousVal = previous.doubleValue();
        double change = ((currentVal - previousVal) / previousVal) * 100;
        
        // Round to 1 decimal place
        return Math.round(change * 10.0) / 10.0;
    }

    /**
     * Determine change direction based on change value.
     */
    private String determineChangeDirection(Double change) {
        if (change == null || change == 0) {
            return null;
        }
        return change > 0 ? DIRECTION_UP : DIRECTION_DOWN;
    }

    /**
     * Convert cents to euros with proper decimal handling.
     */
    private BigDecimal convertCentsToEuros(Long cents) {
        if (cents == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(cents)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Internal class to hold aggregated metrics.
     */
    private static class AggregatedMetrics {
        int totalBikeRentals = 0;
        long totalRevenueCents = 0L;
        int activeCustomers = 0;
        int newRegistrations = 0;
        long totalBikeRentalDurationMinutes = 0L;
        int averageBikeRentalDurationMinutes = 0;
        int totalBikes = 0;
        int inUseBikes = 0;
    }

    /**
     * Internal class to hold real-time metrics from rental-service.
     */
    private static class RealTimeMetrics {
        int totalBikeRentals = 0;
        int completedBikeRentals = 0;
        int cancelledBikeRentals = 0;
        long totalRevenueCents = 0L;
        int activeCustomers = 0;
        int newRegistrations = 0;
        long totalBikeRentalDurationMinutes = 0L;
        int averageBikeRentalDurationMinutes = 0;
        int totalBikes = 0;
        int inUseBikes = 0;
    }
}
