package org.clickenrent.analyticsservice.repository;

import org.clickenrent.analyticsservice.entity.AnalyticsLocationMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AnalyticsLocationMetrics entity.
 * Provides query methods for location-based metrics retrieval.
 */
@Repository
public interface AnalyticsLocationMetricsRepository extends JpaRepository<AnalyticsLocationMetrics, Long> {

    /**
     * Find metrics by external ID for cross-service communication
     */
    Optional<AnalyticsLocationMetrics> findByExternalId(String externalId);

    /**
     * Find metrics by company, date, and location
     */
    Optional<AnalyticsLocationMetrics> findByCompanyExternalIdAndMetricDateAndLocationExternalId(
            String companyExternalId, LocalDate metricDate, String locationExternalId);

    /**
     * Find all metrics for a specific company
     */
    List<AnalyticsLocationMetrics> findByCompanyExternalId(String companyExternalId);

    /**
     * Find all metrics for a specific location
     */
    List<AnalyticsLocationMetrics> findByLocationExternalId(String locationExternalId);

    /**
     * Find all metrics for a specific location and company
     */
    List<AnalyticsLocationMetrics> findByCompanyExternalIdAndLocationExternalId(
            String companyExternalId, String locationExternalId);

    /**
     * Find metrics for a specific date across all locations (all companies)
     * Note: Hibernate filters will apply tenant isolation automatically
     */
    List<AnalyticsLocationMetrics> findByMetricDate(LocalDate metricDate);

    /**
     * Find metrics for a specific date and company across all locations
     */
    List<AnalyticsLocationMetrics> findByCompanyExternalIdAndMetricDate(
            String companyExternalId, LocalDate metricDate);

    /**
     * Find metrics within a date range for all locations (all companies)
     * Note: Hibernate filters will apply tenant isolation automatically
     */
    List<AnalyticsLocationMetrics> findByMetricDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find metrics within a date range for a specific company
     */
    List<AnalyticsLocationMetrics> findByCompanyExternalIdAndMetricDateBetween(
            String companyExternalId, LocalDate startDate, LocalDate endDate);

    /**
     * Find metrics within a date range for a specific location
     */
    List<AnalyticsLocationMetrics> findByLocationExternalIdAndMetricDateBetween(
            String locationExternalId, LocalDate startDate, LocalDate endDate);

    /**
     * Find metrics within a date range for a specific location and company
     */
    List<AnalyticsLocationMetrics> findByCompanyExternalIdAndLocationExternalIdAndMetricDateBetween(
            String companyExternalId, String locationExternalId, LocalDate startDate, LocalDate endDate);

    /**
     * Find the latest metrics for a specific location
     */
    Optional<AnalyticsLocationMetrics> findTopByLocationExternalIdOrderByMetricDateDesc(String locationExternalId);

    /**
     * Find the latest metrics for a specific location and company
     */
    Optional<AnalyticsLocationMetrics> findTopByCompanyExternalIdAndLocationExternalIdOrderByMetricDateDesc(
            String companyExternalId, String locationExternalId);

    /**
     * Check if metrics exist by external ID
     */
    boolean existsByExternalId(String externalId);

    /**
     * Check if metrics exist for a company, date, and location
     */
    boolean existsByCompanyExternalIdAndMetricDateAndLocationExternalId(
            String companyExternalId, LocalDate metricDate, String locationExternalId);
}
