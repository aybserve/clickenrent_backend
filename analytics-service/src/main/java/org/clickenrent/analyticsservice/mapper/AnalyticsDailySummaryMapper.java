package org.clickenrent.analyticsservice.mapper;

import org.clickenrent.analyticsservice.dto.AnalyticsDailySummaryDTO;
import org.clickenrent.analyticsservice.entity.AnalyticsDailySummary;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between AnalyticsDailySummary entity and AnalyticsDailySummaryDTO.
 */
@Component
public class AnalyticsDailySummaryMapper {

    /**
     * Convert entity to DTO
     */
    public AnalyticsDailySummaryDTO toDto(AnalyticsDailySummary entity) {
        if (entity == null) {
            return null;
        }

        return AnalyticsDailySummaryDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .companyExternalId(entity.getCompanyExternalId())
                .summaryDate(entity.getSummaryDate())
                // User Metrics
                .newCustomers(entity.getNewCustomers())
                .activeCustomers(entity.getActiveCustomers())
                .totalCustomers(entity.getTotalCustomers())
                // Bike Rental Metrics
                .totalBikeRentals(entity.getTotalBikeRentals())
                .completedBikeRentals(entity.getCompletedBikeRentals())
                .cancelledBikeRentals(entity.getCancelledBikeRentals())
                .totalBikeRentalDurationMinutes(entity.getTotalBikeRentalDurationMinutes())
                .averageBikeRentalDurationMinutes(entity.getAverageBikeRentalDurationMinutes())
                // Revenue Metrics
                .totalRevenueCents(entity.getTotalRevenueCents())
                .totalRefundsCents(entity.getTotalRefundsCents())
                .averageBikeRentalRevenueCents(entity.getAverageBikeRentalRevenueCents())
                // Fleet Metrics
                .totalBikes(entity.getTotalBikes())
                .availableBikes(entity.getAvailableBikes())
                .inUseBikes(entity.getInUseBikes())
                .maintenanceBikes(entity.getMaintenanceBikes())
                // Location Metrics
                .totalLocations(entity.getTotalLocations())
                .activeLocations(entity.getActiveLocations())
                // Support Metrics
                .newTickets(entity.getNewTickets())
                .resolvedTickets(entity.getResolvedTickets())
                .openTickets(entity.getOpenTickets())
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
    public AnalyticsDailySummary toEntity(AnalyticsDailySummaryDTO dto) {
        if (dto == null) {
            return null;
        }

        return AnalyticsDailySummary.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .companyExternalId(dto.getCompanyExternalId())
                .summaryDate(dto.getSummaryDate())
                // User Metrics
                .newCustomers(dto.getNewCustomers())
                .activeCustomers(dto.getActiveCustomers())
                .totalCustomers(dto.getTotalCustomers())
                // Bike Rental Metrics
                .totalBikeRentals(dto.getTotalBikeRentals())
                .completedBikeRentals(dto.getCompletedBikeRentals())
                .cancelledBikeRentals(dto.getCancelledBikeRentals())
                .totalBikeRentalDurationMinutes(dto.getTotalBikeRentalDurationMinutes())
                .averageBikeRentalDurationMinutes(dto.getAverageBikeRentalDurationMinutes())
                // Revenue Metrics
                .totalRevenueCents(dto.getTotalRevenueCents())
                .totalRefundsCents(dto.getTotalRefundsCents())
                .averageBikeRentalRevenueCents(dto.getAverageBikeRentalRevenueCents())
                // Fleet Metrics
                .totalBikes(dto.getTotalBikes())
                .availableBikes(dto.getAvailableBikes())
                .inUseBikes(dto.getInUseBikes())
                .maintenanceBikes(dto.getMaintenanceBikes())
                // Location Metrics
                .totalLocations(dto.getTotalLocations())
                .activeLocations(dto.getActiveLocations())
                // Support Metrics
                .newTickets(dto.getNewTickets())
                .resolvedTickets(dto.getResolvedTickets())
                .openTickets(dto.getOpenTickets())
                .build();
    }

    /**
     * Update entity from DTO (for partial updates)
     */
    public void updateEntityFromDto(AnalyticsDailySummaryDTO dto, AnalyticsDailySummary entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Note: ID, externalId, companyExternalId, and summaryDate should not be updated

        // User Metrics
        if (dto.getNewCustomers() != null) {
            entity.setNewCustomers(dto.getNewCustomers());
        }
        if (dto.getActiveCustomers() != null) {
            entity.setActiveCustomers(dto.getActiveCustomers());
        }
        if (dto.getTotalCustomers() != null) {
            entity.setTotalCustomers(dto.getTotalCustomers());
        }

        // Bike Rental Metrics
        if (dto.getTotalBikeRentals() != null) {
            entity.setTotalBikeRentals(dto.getTotalBikeRentals());
        }
        if (dto.getCompletedBikeRentals() != null) {
            entity.setCompletedBikeRentals(dto.getCompletedBikeRentals());
        }
        if (dto.getCancelledBikeRentals() != null) {
            entity.setCancelledBikeRentals(dto.getCancelledBikeRentals());
        }
        if (dto.getTotalBikeRentalDurationMinutes() != null) {
            entity.setTotalBikeRentalDurationMinutes(dto.getTotalBikeRentalDurationMinutes());
        }
        if (dto.getAverageBikeRentalDurationMinutes() != null) {
            entity.setAverageBikeRentalDurationMinutes(dto.getAverageBikeRentalDurationMinutes());
        }

        // Revenue Metrics
        if (dto.getTotalRevenueCents() != null) {
            entity.setTotalRevenueCents(dto.getTotalRevenueCents());
        }
        if (dto.getTotalRefundsCents() != null) {
            entity.setTotalRefundsCents(dto.getTotalRefundsCents());
        }
        if (dto.getAverageBikeRentalRevenueCents() != null) {
            entity.setAverageBikeRentalRevenueCents(dto.getAverageBikeRentalRevenueCents());
        }

        // Fleet Metrics
        if (dto.getTotalBikes() != null) {
            entity.setTotalBikes(dto.getTotalBikes());
        }
        if (dto.getAvailableBikes() != null) {
            entity.setAvailableBikes(dto.getAvailableBikes());
        }
        if (dto.getInUseBikes() != null) {
            entity.setInUseBikes(dto.getInUseBikes());
        }
        if (dto.getMaintenanceBikes() != null) {
            entity.setMaintenanceBikes(dto.getMaintenanceBikes());
        }

        // Location Metrics
        if (dto.getTotalLocations() != null) {
            entity.setTotalLocations(dto.getTotalLocations());
        }
        if (dto.getActiveLocations() != null) {
            entity.setActiveLocations(dto.getActiveLocations());
        }

        // Support Metrics
        if (dto.getNewTickets() != null) {
            entity.setNewTickets(dto.getNewTickets());
        }
        if (dto.getResolvedTickets() != null) {
            entity.setResolvedTickets(dto.getResolvedTickets());
        }
        if (dto.getOpenTickets() != null) {
            entity.setOpenTickets(dto.getOpenTickets());
        }
    }
}
