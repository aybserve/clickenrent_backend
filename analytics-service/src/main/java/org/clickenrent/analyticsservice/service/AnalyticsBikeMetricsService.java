package org.clickenrent.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.analyticsservice.dto.AnalyticsBikeMetricsDTO;
import org.clickenrent.analyticsservice.entity.AnalyticsBikeMetrics;
import org.clickenrent.analyticsservice.exception.DuplicateResourceException;
import org.clickenrent.analyticsservice.exception.ResourceNotFoundException;
import org.clickenrent.analyticsservice.exception.UnauthorizedException;
import org.clickenrent.analyticsservice.mapper.AnalyticsBikeMetricsMapper;
import org.clickenrent.analyticsservice.repository.AnalyticsBikeMetricsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for managing AnalyticsBikeMetrics entities with security checks.
 * Implements role-based access control and multi-tenant isolation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsBikeMetricsService {

    private final AnalyticsBikeMetricsRepository repository;
    private final AnalyticsBikeMetricsMapper mapper;
    private final SecurityService securityService;

    /**
     * Get all bike metrics with pagination and role-based filtering.
     * Admin sees all, B2B sees their companies, Customer has no access.
     */
    @Transactional(readOnly = true)
    public Page<AnalyticsBikeMetricsDTO> getAllMetrics(Pageable pageable) {
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
                List<AnalyticsBikeMetrics> metrics = repository.findAll();
                
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), metrics.size());
                List<AnalyticsBikeMetricsDTO> pageContent = metrics.subList(start, end).stream()
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
    public AnalyticsBikeMetricsDTO getMetricsById(Long id) {
        AnalyticsBikeMetrics metrics = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnalyticsBikeMetrics", "id", id));

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
    public AnalyticsBikeMetricsDTO getMetricsByExternalId(String externalId) {
        AnalyticsBikeMetrics metrics = repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("AnalyticsBikeMetrics", "externalId", externalId));

        // Check if user has access to this metrics' company
        if (!securityService.isAdmin() &&
            !securityService.hasAccessToCompanyByExternalId(metrics.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to view this analytics metrics");
        }

        return mapper.toDto(metrics);
    }

    /**
     * Get metrics by date and bike for a specific company
     */
    @Transactional(readOnly = true)
    public AnalyticsBikeMetricsDTO getMetricsByDateAndBike(
            LocalDate date, String bikeExternalId, String companyExternalId) {
        
        if (companyExternalId == null) {
            throw new IllegalArgumentException("Company external ID is required");
        }
        if (bikeExternalId == null) {
            throw new IllegalArgumentException("Bike external ID is required");
        }

        // Check if user has access to this company
        if (!securityService.isAdmin() &&
            !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
            throw new UnauthorizedException("You don't have permission to view this company's analytics");
        }

        AnalyticsBikeMetrics metrics = repository.findByCompanyExternalIdAndMetricDateAndBikeExternalId(
                companyExternalId, date, bikeExternalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AnalyticsBikeMetrics", 
                        "companyExternalId, date and bikeExternalId", 
                        companyExternalId + ", " + date + " and " + bikeExternalId));

        return mapper.toDto(metrics);
    }

    /**
     * Get all metrics for a specific bike
     */
    @Transactional(readOnly = true)
    public List<AnalyticsBikeMetricsDTO> getMetricsByBike(String bikeExternalId, String companyExternalId) {
        if (bikeExternalId == null) {
            throw new IllegalArgumentException("Bike external ID is required");
        }

        List<AnalyticsBikeMetrics> metrics;

        if (companyExternalId != null) {
            // Check if user has access to this company
            if (!securityService.isAdmin() &&
                !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
                throw new UnauthorizedException("You don't have permission to view this company's analytics");
            }
            
            metrics = repository.findByCompanyExternalIdAndBikeExternalId(companyExternalId, bikeExternalId);
        } else {
            // Only admins can query without company filter
            if (!securityService.isAdmin()) {
                throw new UnauthorizedException("Company external ID is required for non-admin users");
            }
            
            metrics = repository.findByBikeExternalId(bikeExternalId);
        }

        return metrics.stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Get metrics by bike code
     */
    @Transactional(readOnly = true)
    public List<AnalyticsBikeMetricsDTO> getMetricsByBikeCode(String bikeCode, String companyExternalId) {
        if (bikeCode == null) {
            throw new IllegalArgumentException("Bike code is required");
        }

        List<AnalyticsBikeMetrics> metrics;

        if (companyExternalId != null) {
            // Check if user has access to this company
            if (!securityService.isAdmin() &&
                !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
                throw new UnauthorizedException("You don't have permission to view this company's analytics");
            }
            
            metrics = repository.findByCompanyExternalIdAndBikeCode(companyExternalId, bikeCode);
        } else {
            // Only admins can query without company filter
            if (!securityService.isAdmin()) {
                throw new UnauthorizedException("Company external ID is required for non-admin users");
            }
            
            metrics = repository.findByBikeCode(bikeCode);
        }

        return metrics.stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Get metrics for a specific date
     */
    @Transactional(readOnly = true)
    public List<AnalyticsBikeMetricsDTO> getMetricsByDate(LocalDate date, String companyExternalId) {
        if (date == null) {
            throw new IllegalArgumentException("Date is required");
        }

        List<AnalyticsBikeMetrics> metrics;

        if (companyExternalId != null) {
            // Check if user has access to this company
            if (!securityService.isAdmin() &&
                !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
                throw new UnauthorizedException("You don't have permission to view this company's analytics");
            }
            
            metrics = repository.findByCompanyExternalIdAndMetricDate(companyExternalId, date);
        } else {
            // Only admins can query across all companies
            if (!securityService.isAdmin()) {
                throw new UnauthorizedException("Only administrators can view analytics across all companies");
            }
            
            metrics = repository.findByMetricDate(date);
        }

        return metrics.stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Get metrics within a date range for a specific bike
     */
    @Transactional(readOnly = true)
    public List<AnalyticsBikeMetricsDTO> getMetricsBetweenDates(
            LocalDate startDate, LocalDate endDate, String bikeExternalId, String companyExternalId) {
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        List<AnalyticsBikeMetrics> metrics;

        if (bikeExternalId != null && companyExternalId != null) {
            // Check if user has access to this company
            if (!securityService.isAdmin() &&
                !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
                throw new UnauthorizedException("You don't have permission to view this company's analytics");
            }
            
            metrics = repository.findByCompanyExternalIdAndBikeExternalIdAndMetricDateBetween(
                    companyExternalId, bikeExternalId, startDate, endDate);
        } else if (bikeExternalId != null) {
            // Bike specified, no company filter (admin only)
            if (!securityService.isAdmin()) {
                throw new UnauthorizedException("Company external ID is required for non-admin users");
            }
            
            metrics = repository.findByBikeExternalIdAndMetricDateBetween(bikeExternalId, startDate, endDate);
        } else if (companyExternalId != null) {
            // Company specified, all bikes
            if (!securityService.isAdmin() &&
                !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
                throw new UnauthorizedException("You don't have permission to view this company's analytics");
            }
            
            metrics = repository.findByCompanyExternalIdAndMetricDateBetween(companyExternalId, startDate, endDate);
        } else {
            // No filters (admin only)
            if (!securityService.isAdmin()) {
                throw new UnauthorizedException("Only administrators can view analytics across all companies");
            }
            
            metrics = repository.findByMetricDateBetween(startDate, endDate);
        }

        return metrics.stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Get the latest metrics for a specific bike
     */
    @Transactional(readOnly = true)
    public AnalyticsBikeMetricsDTO getLatestMetrics(String bikeExternalId, String companyExternalId) {
        if (bikeExternalId == null) {
            throw new IllegalArgumentException("Bike external ID is required");
        }

        AnalyticsBikeMetrics metrics;

        if (companyExternalId != null) {
            // Check if user has access to this company
            if (!securityService.isAdmin() &&
                !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
                throw new UnauthorizedException("You don't have permission to view this company's analytics");
            }
            
            metrics = repository.findTopByCompanyExternalIdAndBikeExternalIdOrderByMetricDateDesc(
                    companyExternalId, bikeExternalId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "AnalyticsBikeMetrics", 
                            "companyExternalId and bikeExternalId", 
                            companyExternalId + " and " + bikeExternalId));
        } else {
            // Only admins can query without company filter
            if (!securityService.isAdmin()) {
                throw new UnauthorizedException("Company external ID is required for non-admin users");
            }
            
            metrics = repository.findTopByBikeExternalIdOrderByMetricDateDesc(bikeExternalId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "AnalyticsBikeMetrics", 
                            "bikeExternalId", 
                            bikeExternalId));
        }

        return mapper.toDto(metrics);
    }

    /**
     * Create new analytics bike metrics (admin only)
     */
    @Transactional
    public AnalyticsBikeMetricsDTO createMetrics(AnalyticsBikeMetricsDTO dto) {
        // Only admins can create analytics metrics
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create analytics metrics");
        }

        // Check for duplicate
        if (repository.existsByCompanyExternalIdAndMetricDateAndBikeExternalId(
                dto.getCompanyExternalId(), dto.getMetricDate(), dto.getBikeExternalId())) {
            throw new DuplicateResourceException(
                    "Analytics bike metrics already exist for company " + dto.getCompanyExternalId() + 
                    ", date " + dto.getMetricDate() + " and bike " + dto.getBikeExternalId());
        }

        AnalyticsBikeMetrics metrics = mapper.toEntity(dto);
        metrics.sanitizeForCreate();
        metrics = repository.save(metrics);
        
        log.info("Created analytics bike metrics: id={}, companyExternalId={}, date={}, bikeExternalId={}", 
                metrics.getId(), metrics.getCompanyExternalId(), metrics.getMetricDate(), metrics.getBikeExternalId());
        
        return mapper.toDto(metrics);
    }

    /**
     * Update existing analytics bike metrics (admin only)
     */
    @Transactional
    public AnalyticsBikeMetricsDTO updateMetrics(Long id, AnalyticsBikeMetricsDTO dto) {
        // Only admins can update analytics metrics
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update analytics metrics");
        }

        AnalyticsBikeMetrics metrics = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnalyticsBikeMetrics", "id", id));

        mapper.updateEntityFromDto(dto, metrics);
        metrics = repository.save(metrics);
        
        log.info("Updated analytics bike metrics: id={}, companyExternalId={}, date={}, bikeExternalId={}", 
                metrics.getId(), metrics.getCompanyExternalId(), metrics.getMetricDate(), metrics.getBikeExternalId());
        
        return mapper.toDto(metrics);
    }

    /**
     * Delete analytics bike metrics (admin only, soft delete)
     */
    @Transactional
    public void deleteMetrics(Long id) {
        // Only admins can delete analytics metrics
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete analytics metrics");
        }

        AnalyticsBikeMetrics metrics = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnalyticsBikeMetrics", "id", id));

        repository.delete(metrics); // Triggers soft delete via @SQLDelete
        
        log.info("Deleted analytics bike metrics: id={}, companyExternalId={}, date={}, bikeExternalId={}", 
                id, metrics.getCompanyExternalId(), metrics.getMetricDate(), metrics.getBikeExternalId());
    }

    /**
     * Check if metrics exist by external ID
     */
    @Transactional(readOnly = true)
    public boolean existsByExternalId(String externalId) {
        return repository.existsByExternalId(externalId);
    }
}
