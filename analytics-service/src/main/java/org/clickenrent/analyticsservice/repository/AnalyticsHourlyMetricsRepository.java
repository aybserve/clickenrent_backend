package org.clickenrent.analyticsservice.repository;

import org.clickenrent.analyticsservice.entity.AnalyticsHourlyMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AnalyticsHourlyMetrics entity.
 * Provides query methods for hourly metrics retrieval.
 */
@Repository
public interface AnalyticsHourlyMetricsRepository extends JpaRepository<AnalyticsHourlyMetrics, Long> {

    /**
     * Find metrics by external ID for cross-service communication
     */
    Optional<AnalyticsHourlyMetrics> findByExternalId(String externalId);

    /**
     * Find metrics by company and specific hour
     */
    Optional<AnalyticsHourlyMetrics> findByCompanyExternalIdAndMetricHour(
            String companyExternalId, ZonedDateTime metricHour);

    /**
     * Find all metrics for a specific company
     */
    List<AnalyticsHourlyMetrics> findByCompanyExternalId(String companyExternalId);

    /**
     * Find metrics within a time range (all companies)
     * Note: Hibernate filters will apply tenant isolation automatically
     */
    List<AnalyticsHourlyMetrics> findByMetricHourBetween(ZonedDateTime startHour, ZonedDateTime endHour);

    /**
     * Find metrics within a time range for a specific company
     */
    List<AnalyticsHourlyMetrics> findByCompanyExternalIdAndMetricHourBetween(
            String companyExternalId, ZonedDateTime startHour, ZonedDateTime endHour);

    /**
     * Find the latest metrics for a specific company
     */
    Optional<AnalyticsHourlyMetrics> findTopByCompanyExternalIdOrderByMetricHourDesc(String companyExternalId);

    /**
     * Find the latest metrics across all companies (admin only)
     * Note: Hibernate filters will apply tenant isolation automatically
     */
    Optional<AnalyticsHourlyMetrics> findTopByOrderByMetricHourDesc();

    /**
     * Check if metrics exist by external ID
     */
    boolean existsByExternalId(String externalId);

    /**
     * Check if metrics exist for a company at a specific hour
     */
    boolean existsByCompanyExternalIdAndMetricHour(String companyExternalId, ZonedDateTime metricHour);
}
