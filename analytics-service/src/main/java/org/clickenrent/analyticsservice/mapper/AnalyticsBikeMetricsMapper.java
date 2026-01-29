package org.clickenrent.analyticsservice.mapper;

import org.clickenrent.analyticsservice.dto.AnalyticsBikeMetricsDTO;
import org.clickenrent.analyticsservice.entity.AnalyticsBikeMetrics;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between AnalyticsBikeMetrics entity and AnalyticsBikeMetricsDTO.
 */
@Component
public class AnalyticsBikeMetricsMapper {

    /**
     * Convert entity to DTO
     */
    public AnalyticsBikeMetricsDTO toDto(AnalyticsBikeMetrics entity) {
        if (entity == null) {
            return null;
        }

        return AnalyticsBikeMetricsDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .companyExternalId(entity.getCompanyExternalId())
                .metricDate(entity.getMetricDate())
                .bikeExternalId(entity.getBikeExternalId())
                .bikeCode(entity.getBikeCode())
                // Metrics
                .totalBikeRentals(entity.getTotalBikeRentals())
                .totalDurationMinutes(entity.getTotalDurationMinutes())
                .bikeRentalRevenueCents(entity.getBikeRentalRevenueCents())
                // Status tracking
                .availableHours(entity.getAvailableHours())
                .inUseHours(entity.getInUseHours())
                .maintenanceHours(entity.getMaintenanceHours())
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
    public AnalyticsBikeMetrics toEntity(AnalyticsBikeMetricsDTO dto) {
        if (dto == null) {
            return null;
        }

        return AnalyticsBikeMetrics.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .companyExternalId(dto.getCompanyExternalId())
                .metricDate(dto.getMetricDate())
                .bikeExternalId(dto.getBikeExternalId())
                .bikeCode(dto.getBikeCode())
                // Metrics
                .totalBikeRentals(dto.getTotalBikeRentals())
                .totalDurationMinutes(dto.getTotalDurationMinutes())
                .bikeRentalRevenueCents(dto.getBikeRentalRevenueCents())
                // Status tracking
                .availableHours(dto.getAvailableHours())
                .inUseHours(dto.getInUseHours())
                .maintenanceHours(dto.getMaintenanceHours())
                .build();
    }

    /**
     * Update entity from DTO (for partial updates)
     */
    public void updateEntityFromDto(AnalyticsBikeMetricsDTO dto, AnalyticsBikeMetrics entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Note: ID, externalId, companyExternalId, metricDate, bikeExternalId, and bikeCode should not be updated

        // Metrics
        if (dto.getTotalBikeRentals() != null) {
            entity.setTotalBikeRentals(dto.getTotalBikeRentals());
        }
        if (dto.getTotalDurationMinutes() != null) {
            entity.setTotalDurationMinutes(dto.getTotalDurationMinutes());
        }
        if (dto.getBikeRentalRevenueCents() != null) {
            entity.setBikeRentalRevenueCents(dto.getBikeRentalRevenueCents());
        }

        // Status tracking
        if (dto.getAvailableHours() != null) {
            entity.setAvailableHours(dto.getAvailableHours());
        }
        if (dto.getInUseHours() != null) {
            entity.setInUseHours(dto.getInUseHours());
        }
        if (dto.getMaintenanceHours() != null) {
            entity.setMaintenanceHours(dto.getMaintenanceHours());
        }
    }
}
