package org.clickenrent.analyticsservice.repository;

import org.clickenrent.analyticsservice.entity.AnalyticsBikeMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AnalyticsBikeMetrics entity.
 * Provides query methods for bike-based metrics retrieval.
 */
@Repository
public interface AnalyticsBikeMetricsRepository extends JpaRepository<AnalyticsBikeMetrics, Long> {

    /**
     * Find metrics by external ID for cross-service communication
     */
    Optional<AnalyticsBikeMetrics> findByExternalId(String externalId);

    /**
     * Find metrics by company, date, and bike
     */
    Optional<AnalyticsBikeMetrics> findByCompanyExternalIdAndMetricDateAndBikeExternalId(
            String companyExternalId, LocalDate metricDate, String bikeExternalId);

    /**
     * Find all metrics for a specific company
     */
    List<AnalyticsBikeMetrics> findByCompanyExternalId(String companyExternalId);

    /**
     * Find all metrics for a specific bike
     */
    List<AnalyticsBikeMetrics> findByBikeExternalId(String bikeExternalId);

    /**
     * Find all metrics for a specific bike and company
     */
    List<AnalyticsBikeMetrics> findByCompanyExternalIdAndBikeExternalId(
            String companyExternalId, String bikeExternalId);

    /**
     * Find metrics by bike code
     */
    List<AnalyticsBikeMetrics> findByBikeCode(String bikeCode);

    /**
     * Find metrics by bike code and company
     */
    List<AnalyticsBikeMetrics> findByCompanyExternalIdAndBikeCode(String companyExternalId, String bikeCode);

    /**
     * Find metrics for a specific date across all bikes (all companies)
     * Note: Hibernate filters will apply tenant isolation automatically
     */
    List<AnalyticsBikeMetrics> findByMetricDate(LocalDate metricDate);

    /**
     * Find metrics for a specific date and company across all bikes
     */
    List<AnalyticsBikeMetrics> findByCompanyExternalIdAndMetricDate(
            String companyExternalId, LocalDate metricDate);

    /**
     * Find metrics within a date range for all bikes (all companies)
     * Note: Hibernate filters will apply tenant isolation automatically
     */
    List<AnalyticsBikeMetrics> findByMetricDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find metrics within a date range for a specific company
     */
    List<AnalyticsBikeMetrics> findByCompanyExternalIdAndMetricDateBetween(
            String companyExternalId, LocalDate startDate, LocalDate endDate);

    /**
     * Find metrics within a date range for a specific bike
     */
    List<AnalyticsBikeMetrics> findByBikeExternalIdAndMetricDateBetween(
            String bikeExternalId, LocalDate startDate, LocalDate endDate);

    /**
     * Find metrics within a date range for a specific bike and company
     */
    List<AnalyticsBikeMetrics> findByCompanyExternalIdAndBikeExternalIdAndMetricDateBetween(
            String companyExternalId, String bikeExternalId, LocalDate startDate, LocalDate endDate);

    /**
     * Find the latest metrics for a specific bike
     */
    Optional<AnalyticsBikeMetrics> findTopByBikeExternalIdOrderByMetricDateDesc(String bikeExternalId);

    /**
     * Find the latest metrics for a specific bike and company
     */
    Optional<AnalyticsBikeMetrics> findTopByCompanyExternalIdAndBikeExternalIdOrderByMetricDateDesc(
            String companyExternalId, String bikeExternalId);

    /**
     * Check if metrics exist by external ID
     */
    boolean existsByExternalId(String externalId);

    /**
     * Check if metrics exist for a company, date, and bike
     */
    boolean existsByCompanyExternalIdAndMetricDateAndBikeExternalId(
            String companyExternalId, LocalDate metricDate, String bikeExternalId);
}
