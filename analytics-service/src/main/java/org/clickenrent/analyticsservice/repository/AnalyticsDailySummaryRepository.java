package org.clickenrent.analyticsservice.repository;

import org.clickenrent.analyticsservice.entity.AnalyticsDailySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AnalyticsDailySummary entity.
 * Provides query methods for date-based analytics retrieval.
 */
@Repository
public interface AnalyticsDailySummaryRepository extends JpaRepository<AnalyticsDailySummary, Long> {

    /**
     * Find summary by external ID for cross-service communication
     */
    Optional<AnalyticsDailySummary> findByExternalId(String externalId);

    /**
     * Find summary by company and specific date
     */
    Optional<AnalyticsDailySummary> findByCompanyExternalIdAndSummaryDate(
            String companyExternalId, LocalDate summaryDate);

    /**
     * Find all summaries for a specific company
     */
    List<AnalyticsDailySummary> findByCompanyExternalId(String companyExternalId);

    /**
     * Find summaries within a date range (all companies)
     * Note: Hibernate filters will apply tenant isolation automatically
     */
    List<AnalyticsDailySummary> findBySummaryDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find summaries within a date range for a specific company
     */
    List<AnalyticsDailySummary> findByCompanyExternalIdAndSummaryDateBetween(
            String companyExternalId, LocalDate startDate, LocalDate endDate);

    /**
     * Find the latest summary for a specific company
     */
    Optional<AnalyticsDailySummary> findTopByCompanyExternalIdOrderBySummaryDateDesc(String companyExternalId);

    /**
     * Find the latest summary across all companies (admin only)
     * Note: Hibernate filters will apply tenant isolation automatically
     */
    Optional<AnalyticsDailySummary> findTopByOrderBySummaryDateDesc();

    /**
     * Check if summary exists by external ID
     */
    boolean existsByExternalId(String externalId);

    /**
     * Check if summary exists for a company on a specific date
     */
    boolean existsByCompanyExternalIdAndSummaryDate(String companyExternalId, LocalDate summaryDate);
}
