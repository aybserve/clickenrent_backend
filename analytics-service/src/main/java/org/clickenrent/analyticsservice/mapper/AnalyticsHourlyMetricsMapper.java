package org.clickenrent.analyticsservice.mapper;

import org.clickenrent.analyticsservice.dto.AnalyticsHourlyMetricsDTO;
import org.clickenrent.analyticsservice.entity.AnalyticsHourlyMetrics;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between AnalyticsHourlyMetrics entity and AnalyticsHourlyMetricsDTO.
 */
@Component
public class AnalyticsHourlyMetricsMapper {

    /**
     * Convert entity to DTO
     */
    public AnalyticsHourlyMetricsDTO toDto(AnalyticsHourlyMetrics entity) {
        if (entity == null) {
            return null;
        }

        return AnalyticsHourlyMetricsDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .companyExternalId(entity.getCompanyExternalId())
                .metricHour(entity.getMetricHour())
                // Metrics
                .bikeRentalsStarted(entity.getBikeRentalsStarted())
                .bikeRentalsCompleted(entity.getBikeRentalsCompleted())
                .bikeRentalRevenueCents(entity.getBikeRentalRevenueCents())
                .activeCustomers(entity.getActiveCustomers())
                .newRegistrations(entity.getNewRegistrations())
                // Audit fields
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    /**
     * Convert DTO to entity
     */
    public AnalyticsHourlyMetrics toEntity(AnalyticsHourlyMetricsDTO dto) {
        if (dto == null) {
            return null;
        }

        return AnalyticsHourlyMetrics.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .companyExternalId(dto.getCompanyExternalId())
                .metricHour(dto.getMetricHour())
                // Metrics
                .bikeRentalsStarted(dto.getBikeRentalsStarted())
                .bikeRentalsCompleted(dto.getBikeRentalsCompleted())
                .bikeRentalRevenueCents(dto.getBikeRentalRevenueCents())
                .activeCustomers(dto.getActiveCustomers())
                .newRegistrations(dto.getNewRegistrations())
                .build();
    }

    /**
     * Update entity from DTO (for partial updates)
     */
    public void updateEntityFromDto(AnalyticsHourlyMetricsDTO dto, AnalyticsHourlyMetrics entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Note: ID, externalId, companyExternalId, and metricHour should not be updated

        // Metrics
        if (dto.getBikeRentalsStarted() != null) {
            entity.setBikeRentalsStarted(dto.getBikeRentalsStarted());
        }
        if (dto.getBikeRentalsCompleted() != null) {
            entity.setBikeRentalsCompleted(dto.getBikeRentalsCompleted());
        }
        if (dto.getBikeRentalRevenueCents() != null) {
            entity.setBikeRentalRevenueCents(dto.getBikeRentalRevenueCents());
        }
        if (dto.getActiveCustomers() != null) {
            entity.setActiveCustomers(dto.getActiveCustomers());
        }
        if (dto.getNewRegistrations() != null) {
            entity.setNewRegistrations(dto.getNewRegistrations());
        }
    }
}
