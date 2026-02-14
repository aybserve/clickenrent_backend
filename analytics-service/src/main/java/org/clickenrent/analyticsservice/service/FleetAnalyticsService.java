package org.clickenrent.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.analyticsservice.client.RentalServiceClient;
import org.clickenrent.analyticsservice.dto.*;
import org.clickenrent.analyticsservice.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Service for generating fleet analytics including bike status and battery levels.
 * Provides fleet overview and handles multi-tenant data access.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FleetAnalyticsService {

    private final RentalServiceClient rentalServiceClient;
    private final SecurityService securityService;

    // Bike Status IDs based on rental-service data.sql
    private static final Long STATUS_AVAILABLE = 1L;
    private static final Long STATUS_IN_USE = 2L;
    private static final Long STATUS_RESERVED = 3L;
    private static final Long STATUS_BROKEN = 4L;
    private static final Long STATUS_DISABLED = 5L;

    // Battery level ranges
    private static final int BATTERY_CRITICAL_MAX = 24;
    private static final int BATTERY_LOW_MIN = 25;
    private static final int BATTERY_LOW_MAX = 50;
    private static final int BATTERY_MEDIUM_MIN = 51;
    private static final int BATTERY_MEDIUM_MAX = 75;
    private static final int BATTERY_FULL_MIN = 76;

    /**
     * Get fleet analytics including current status and battery levels.
     * Date parameters (from/to) are optional and reserved for future trend analysis.
     *
     * @param from Start date (optional, reserved for future use)
     * @param to End date (optional, reserved for future use)
     * @return Fleet analytics with current status, battery levels, and inspection schedule
     */
    @Transactional(readOnly = true)
    public FleetAnalyticsDTO getFleetAnalytics(LocalDate from, LocalDate to) {
        // Check user permissions
        if (securityService.isCustomer()) {
            throw new UnauthorizedException("Customers don't have access to fleet analytics");
        }

        log.info("Fetching fleet analytics for company");

        // Fetch all bikes from rental-service
        BikePageDTO bikePage = rentalServiceClient.getBikes(0, 1000);
        List<BikeSummaryDTO> bikes = bikePage.getContent() != null ? 
                bikePage.getContent() : Collections.emptyList();

        log.debug("Retrieved {} bikes for fleet analysis", bikes.size());

        // Calculate current status
        CurrentStatusDTO currentStatus = calculateCurrentStatus(bikes);

        // Calculate battery levels
        BatteryLevelsDTO batteryLevels = calculateBatteryLevels(bikes);

        // Build inspection schedule (placeholder implementation)
        BikeInspectionScheduleDTO inspectionSchedule = buildInspectionSchedule(bikes.size());

        return FleetAnalyticsDTO.builder()
                .currentStatus(currentStatus)
                .batteryLevels(batteryLevels)
                .bikeInspectionSchedule(inspectionSchedule)
                .build();
    }

    /**
     * Calculate current fleet status by counting bikes in each status.
     *
     * @param bikes List of bikes
     * @return Current status with counts by bike status
     */
    private CurrentStatusDTO calculateCurrentStatus(List<BikeSummaryDTO> bikes) {
        int totalBikes = bikes.size();
        int available = 0;
        int inUse = 0;
        int reserved = 0;
        int broken = 0;
        int disabled = 0;

        for (BikeSummaryDTO bike : bikes) {
            Long statusId = bike.getBikeStatusId();
            if (statusId == null) {
                continue;
            }

            if (STATUS_AVAILABLE.equals(statusId)) {
                available++;
            } else if (STATUS_IN_USE.equals(statusId)) {
                inUse++;
            } else if (STATUS_RESERVED.equals(statusId)) {
                reserved++;
            } else if (STATUS_BROKEN.equals(statusId)) {
                broken++;
            } else if (STATUS_DISABLED.equals(statusId)) {
                disabled++;
            }
        }

        log.debug("Fleet status - Total: {}, Available: {}, In Use: {}, Reserved: {}, Broken: {}, Disabled: {}",
                totalBikes, available, inUse, reserved, broken, disabled);

        return CurrentStatusDTO.builder()
                .totalBikes(totalBikes)
                .available(available)
                .inUse(inUse)
                .reserved(reserved)
                .broken(broken)
                .disabled(disabled)
                .build();
    }

    /**
     * Calculate battery level distribution by counting bikes in each battery range.
     *
     * @param bikes List of bikes
     * @return Battery levels with counts by battery range
     */
    private BatteryLevelsDTO calculateBatteryLevels(List<BikeSummaryDTO> bikes) {
        int full = 0;      // 76-100%
        int medium = 0;    // 51-75%
        int low = 0;       // 25-50%
        int critical = 0;  // 0-24%

        for (BikeSummaryDTO bike : bikes) {
            Integer batteryLevel = bike.getBatteryLevel();
            if (batteryLevel == null) {
                // Default to 0 if not set
                batteryLevel = 0;
            }

            if (batteryLevel >= BATTERY_FULL_MIN) {
                full++;
            } else if (batteryLevel >= BATTERY_MEDIUM_MIN && batteryLevel <= BATTERY_MEDIUM_MAX) {
                medium++;
            } else if (batteryLevel >= BATTERY_LOW_MIN && batteryLevel <= BATTERY_LOW_MAX) {
                low++;
            } else if (batteryLevel >= 0 && batteryLevel <= BATTERY_CRITICAL_MAX) {
                // 0-24%
                critical++;
            }
        }

        log.debug("Battery levels - Full: {}, Medium: {}, Low: {}, Critical: {}",
                full, medium, low, critical);

        return BatteryLevelsDTO.builder()
                .full(full)
                .medium(medium)
                .low(low)
                .critical(critical)
                .build();
    }

    /**
     * Build bike inspection schedule.
     * Placeholder implementation - returns today's date and total bike count.
     * TODO: Integrate with support-service for actual inspection data.
     *
     * @param totalBikes Total number of bikes in fleet
     * @return Inspection schedule with placeholder data
     */
    private BikeInspectionScheduleDTO buildInspectionSchedule(int totalBikes) {
        LocalDate today = LocalDate.now();

        log.debug("Building inspection schedule - Next revision: {}, Count: {}", today, totalBikes);

        return BikeInspectionScheduleDTO.builder()
                .dateNextOfRevision(today)
                .countOfBikesForInspection(totalBikes)
                .build();
    }
}
