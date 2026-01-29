package org.clickenrent.analyticsservice.mapper;

import org.clickenrent.analyticsservice.dto.AnalyticsLocationMetricsDTO;
import org.clickenrent.analyticsservice.entity.AnalyticsLocationMetrics;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between AnalyticsLocationMetrics entity and AnalyticsLocationMetricsDTO.
 */
@Component
public class AnalyticsLocationMetricsMapper {

    /**
     * Convert entity to DTO
     */
    public AnalyticsLocationMetricsDTO toDto(AnalyticsLocationMetrics entity) {
        if (entity == null) {
            return null;
        }

        return AnalyticsLocationMetricsDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .companyExternalId(entity.getCompanyExternalId())
                .metricDate(entity.getMetricDate())
                .locationExternalId(entity.getLocationExternalId())
                // Metrics
                .totalPickups(entity.getTotalPickups())
                .totalDropoffs(entity.getTotalDropoffs())
                .uniqueCustomers(entity.getUniqueCustomers())
                .bikeRentalRevenueCents(entity.getBikeRentalRevenueCents())
                .averageBikesAvailable(entity.getAverageBikesAvailable())
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
    public AnalyticsLocationMetrics toEntity(AnalyticsLocationMetricsDTO dto) {
        if (dto == null) {
            return null;
        }

        return AnalyticsLocationMetrics.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .companyExternalId(dto.getCompanyExternalId())
                .metricDate(dto.getMetricDate())
                .locationExternalId(dto.getLocationExternalId())
                // Metrics
                .totalPickups(dto.getTotalPickups())
                .totalDropoffs(dto.getTotalDropoffs())
                .uniqueCustomers(dto.getUniqueCustomers())
                .bikeRentalRevenueCents(dto.getBikeRentalRevenueCents())
                .averageBikesAvailable(dto.getAverageBikesAvailable())
                .build();
    }

    /**
     * Update entity from DTO (for partial updates)
     */
    public void updateEntityFromDto(AnalyticsLocationMetricsDTO dto, AnalyticsLocationMetrics entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Note: ID, externalId, companyExternalId, metricDate, and locationExternalId should not be updated

        // Metrics
        if (dto.getTotalPickups() != null) {
            entity.setTotalPickups(dto.getTotalPickups());
        }
        if (dto.getTotalDropoffs() != null) {
            entity.setTotalDropoffs(dto.getTotalDropoffs());
        }
        if (dto.getUniqueCustomers() != null) {
            entity.setUniqueCustomers(dto.getUniqueCustomers());
        }
        if (dto.getBikeRentalRevenueCents() != null) {
            entity.setBikeRentalRevenueCents(dto.getBikeRentalRevenueCents());
        }
        if (dto.getAverageBikesAvailable() != null) {
            entity.setAverageBikesAvailable(dto.getAverageBikesAvailable());
        }
    }
}
