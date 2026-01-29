package org.clickenrent.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.analyticsservice.dto.AnalyticsHourlyMetricsDTO;
import org.clickenrent.analyticsservice.entity.AnalyticsHourlyMetrics;
import org.clickenrent.analyticsservice.exception.DuplicateResourceException;
import org.clickenrent.analyticsservice.exception.ResourceNotFoundException;
import org.clickenrent.analyticsservice.exception.UnauthorizedException;
import org.clickenrent.analyticsservice.mapper.AnalyticsHourlyMetricsMapper;
import org.clickenrent.analyticsservice.repository.AnalyticsHourlyMetricsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Service for managing AnalyticsHourlyMetrics entities with security checks.
 * Implements role-based access control and multi-tenant isolation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsHourlyMetricsService {

    private final AnalyticsHourlyMetricsRepository repository;
    private final AnalyticsHourlyMetricsMapper mapper;
    private final SecurityService securityService;

    /**
     * Get all hourly metrics with pagination and role-based filtering.
     * Admin sees all, B2B sees their companies, Customer has no access.
     */
    @Transactional(readOnly = true)
    public Page<AnalyticsHourlyMetricsDTO> getAllMetrics(Pageable pageable) {
        // Admin can see all metrics
        if (securityService.isAdmin()) {
            return repository.findAll(pageable)
                    .map(mapper::toDto);
        }

        // B2B can see metrics for their companies (Hibernate filter applies automatically)
        if (securityService.isB2B()) {
            List<String> companyExternalIds = securityService.getCurrentUserCompanyExternalIds();
            if (!companyExternalIds.isEmpty()) {
                // Hibernate filter automatically applies tenant isolation
                List<AnalyticsHourlyMetrics> metrics = repository.findAll();
                
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), metrics.size());
                List<AnalyticsHourlyMetricsDTO> pageContent = metrics.subList(start, end).stream()
                        .map(mapper::toDto)
                        .toList();
                
                return new PageImpl<>(pageContent, pageable, metrics.size());
            }
        }

        // Customers don't have access to analytics metrics
        throw new UnauthorizedException("You don't have permission to view analytics metrics");
    }

    /**
     * Get metrics by ID with access check
     */
    @Transactional(readOnly = true)
    public AnalyticsHourlyMetricsDTO getMetricsById(Long id) {
        AnalyticsHourlyMetrics metrics = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnalyticsHourlyMetrics", "id", id));

        // Check if user has access to this metrics' company
        if (!securityService.isAdmin() &&
            !securityService.hasAccessToCompanyByExternalId(metrics.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to view this analytics metrics");
        }

        return mapper.toDto(metrics);
    }

    /**
     * Get metrics by external ID (for cross-service communication)
     */
    @Transactional(readOnly = true)
    public AnalyticsHourlyMetricsDTO getMetricsByExternalId(String externalId) {
        AnalyticsHourlyMetrics metrics = repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("AnalyticsHourlyMetrics", "externalId", externalId));

        // Check if user has access to this metrics' company
        if (!securityService.isAdmin() &&
            !securityService.hasAccessToCompanyByExternalId(metrics.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to view this analytics metrics");
        }

        return mapper.toDto(metrics);
    }

    /**
     * Get metrics by hour for a specific company.
     * If companyExternalId is null and user is admin, returns latest across all companies.
     */
    @Transactional(readOnly = true)
    public AnalyticsHourlyMetricsDTO getMetricsByHour(ZonedDateTime hour, String companyExternalId) {
        if (companyExternalId == null) {
            throw new IllegalArgumentException("Company external ID is required");
        }

        // Check if user has access to this company
        if (!securityService.isAdmin() &&
            !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
            throw new UnauthorizedException("You don't have permission to view this company's analytics");
        }

        AnalyticsHourlyMetrics metrics = repository.findByCompanyExternalIdAndMetricHour(companyExternalId, hour)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AnalyticsHourlyMetrics", 
                        "companyExternalId and hour", 
                        companyExternalId + " and " + hour));

        return mapper.toDto(metrics);
    }

    /**
     * Get metrics within a time range for a specific company
     */
    @Transactional(readOnly = true)
    public List<AnalyticsHourlyMetricsDTO> getMetricsBetweenHours(
            ZonedDateTime startHour, ZonedDateTime endHour, String companyExternalId) {
        
        if (startHour.isAfter(endHour)) {
            throw new IllegalArgumentException("Start hour must be before or equal to end hour");
        }

        List<AnalyticsHourlyMetrics> metrics;

        if (companyExternalId != null) {
            // Check if user has access to this company
            if (!securityService.isAdmin() &&
                !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
                throw new UnauthorizedException("You don't have permission to view this company's analytics");
            }
            
            metrics = repository.findByCompanyExternalIdAndMetricHourBetween(
                    companyExternalId, startHour, endHour);
        } else {
            // Only admins can query across all companies
            if (!securityService.isAdmin()) {
                throw new UnauthorizedException("Only administrators can view analytics across all companies");
            }
            
            metrics = repository.findByMetricHourBetween(startHour, endHour);
        }

        return metrics.stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Get the latest metrics for a specific company
     */
    @Transactional(readOnly = true)
    public AnalyticsHourlyMetricsDTO getLatestMetrics(String companyExternalId) {
        if (companyExternalId == null) {
            // Only admins can get latest metrics across all companies
            if (!securityService.isAdmin()) {
                throw new UnauthorizedException("Company external ID is required for non-admin users");
            }
            
            AnalyticsHourlyMetrics metrics = repository.findTopByOrderByMetricHourDesc()
                    .orElseThrow(() -> new ResourceNotFoundException("No analytics metrics found"));
            
            return mapper.toDto(metrics);
        }

        // Check if user has access to this company
        if (!securityService.isAdmin() &&
            !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
            throw new UnauthorizedException("You don't have permission to view this company's analytics");
        }

        AnalyticsHourlyMetrics metrics = repository.findTopByCompanyExternalIdOrderByMetricHourDesc(companyExternalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AnalyticsHourlyMetrics", 
                        "companyExternalId", 
                        companyExternalId));

        return mapper.toDto(metrics);
    }

    /**
     * Create new analytics metrics (admin only)
     */
    @Transactional
    public AnalyticsHourlyMetricsDTO createMetrics(AnalyticsHourlyMetricsDTO dto) {
        // Only admins can create analytics metrics
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create analytics metrics");
        }

        // Check for duplicate
        if (repository.existsByCompanyExternalIdAndMetricHour(dto.getCompanyExternalId(), dto.getMetricHour())) {
            throw new DuplicateResourceException(
                    "Analytics metrics already exist for company " + 
                    dto.getCompanyExternalId() + " at hour " + dto.getMetricHour());
        }

        AnalyticsHourlyMetrics metrics = mapper.toEntity(dto);
        metrics = repository.save(metrics);
        
        log.info("Created analytics hourly metrics: id={}, companyExternalId={}, hour={}", 
                metrics.getId(), metrics.getCompanyExternalId(), metrics.getMetricHour());
        
        return mapper.toDto(metrics);
    }

    /**
     * Update existing analytics metrics (admin only)
     */
    @Transactional
    public AnalyticsHourlyMetricsDTO updateMetrics(Long id, AnalyticsHourlyMetricsDTO dto) {
        // Only admins can update analytics metrics
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update analytics metrics");
        }

        AnalyticsHourlyMetrics metrics = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnalyticsHourlyMetrics", "id", id));

        mapper.updateEntityFromDto(dto, metrics);
        metrics = repository.save(metrics);
        
        log.info("Updated analytics hourly metrics: id={}, companyExternalId={}, hour={}", 
                metrics.getId(), metrics.getCompanyExternalId(), metrics.getMetricHour());
        
        return mapper.toDto(metrics);
    }

    /**
     * Delete analytics metrics (admin only, soft delete)
     */
    @Transactional
    public void deleteMetrics(Long id) {
        // Only admins can delete analytics metrics
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete analytics metrics");
        }

        AnalyticsHourlyMetrics metrics = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnalyticsHourlyMetrics", "id", id));

        repository.delete(metrics); // Triggers soft delete via @SQLDelete
        
        log.info("Deleted analytics hourly metrics: id={}, companyExternalId={}, hour={}", 
                id, metrics.getCompanyExternalId(), metrics.getMetricHour());
    }

    /**
     * Check if metrics exist by external ID
     */
    @Transactional(readOnly = true)
    public boolean existsByExternalId(String externalId) {
        return repository.existsByExternalId(externalId);
    }
}
