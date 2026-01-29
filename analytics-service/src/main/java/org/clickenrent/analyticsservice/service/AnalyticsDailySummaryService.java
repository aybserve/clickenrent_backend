package org.clickenrent.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.analyticsservice.dto.AnalyticsDailySummaryDTO;
import org.clickenrent.analyticsservice.entity.AnalyticsDailySummary;
import org.clickenrent.analyticsservice.exception.DuplicateResourceException;
import org.clickenrent.analyticsservice.exception.ResourceNotFoundException;
import org.clickenrent.analyticsservice.exception.UnauthorizedException;
import org.clickenrent.analyticsservice.mapper.AnalyticsDailySummaryMapper;
import org.clickenrent.analyticsservice.repository.AnalyticsDailySummaryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for managing AnalyticsDailySummary entities with security checks.
 * Implements role-based access control and multi-tenant isolation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsDailySummaryService {

    private final AnalyticsDailySummaryRepository repository;
    private final AnalyticsDailySummaryMapper mapper;
    private final SecurityService securityService;

    /**
     * Get all summaries with pagination and role-based filtering.
     * Admin sees all, B2B sees their companies, Customer has no access.
     */
    @Transactional(readOnly = true)
    public Page<AnalyticsDailySummaryDTO> getAllSummaries(Pageable pageable) {
        // Admin can see all summaries
        if (securityService.isAdmin()) {
            return repository.findAll(pageable)
                    .map(mapper::toDto);
        }

        // B2B can see summaries for their companies (Hibernate filter applies automatically)
        if (securityService.isB2B()) {
            List<String> companyExternalIds = securityService.getCurrentUserCompanyExternalIds();
            if (!companyExternalIds.isEmpty()) {
                // Hibernate filter automatically applies tenant isolation
                List<AnalyticsDailySummary> summaries = repository.findAll();
                
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), summaries.size());
                List<AnalyticsDailySummaryDTO> pageContent = summaries.subList(start, end).stream()
                        .map(mapper::toDto)
                        .toList();
                
                return new PageImpl<>(pageContent, pageable, summaries.size());
            }
        }

        // Customers don't have access to analytics summaries
        throw new UnauthorizedException("You don't have permission to view analytics summaries");
    }

    /**
     * Get summary by ID with access check
     */
    @Transactional(readOnly = true)
    public AnalyticsDailySummaryDTO getSummaryById(Long id) {
        AnalyticsDailySummary summary = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnalyticsDailySummary", "id", id));

        // Check if user has access to this summary's company
        if (!securityService.isAdmin() &&
            !securityService.hasAccessToCompanyByExternalId(summary.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to view this analytics summary");
        }

        return mapper.toDto(summary);
    }

    /**
     * Get summary by external ID (for cross-service communication)
     */
    @Transactional(readOnly = true)
    public AnalyticsDailySummaryDTO getSummaryByExternalId(String externalId) {
        AnalyticsDailySummary summary = repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("AnalyticsDailySummary", "externalId", externalId));

        // Check if user has access to this summary's company
        if (!securityService.isAdmin() &&
            !securityService.hasAccessToCompanyByExternalId(summary.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to view this analytics summary");
        }

        return mapper.toDto(summary);
    }

    /**
     * Get summary by date for a specific company.
     * If companyExternalId is null and user is admin, returns latest across all companies.
     */
    @Transactional(readOnly = true)
    public AnalyticsDailySummaryDTO getSummaryByDate(LocalDate date, String companyExternalId) {
        if (companyExternalId == null) {
            throw new IllegalArgumentException("Company external ID is required");
        }

        // Check if user has access to this company
        if (!securityService.isAdmin() &&
            !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
            throw new UnauthorizedException("You don't have permission to view this company's analytics");
        }

        AnalyticsDailySummary summary = repository.findByCompanyExternalIdAndSummaryDate(companyExternalId, date)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AnalyticsDailySummary", 
                        "companyExternalId and date", 
                        companyExternalId + " and " + date));

        return mapper.toDto(summary);
    }

    /**
     * Get summaries within a date range for a specific company
     */
    @Transactional(readOnly = true)
    public List<AnalyticsDailySummaryDTO> getSummariesBetweenDates(
            LocalDate startDate, LocalDate endDate, String companyExternalId) {
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        List<AnalyticsDailySummary> summaries;

        if (companyExternalId != null) {
            // Check if user has access to this company
            if (!securityService.isAdmin() &&
                !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
                throw new UnauthorizedException("You don't have permission to view this company's analytics");
            }
            
            summaries = repository.findByCompanyExternalIdAndSummaryDateBetween(
                    companyExternalId, startDate, endDate);
        } else {
            // Only admins can query across all companies
            if (!securityService.isAdmin()) {
                throw new UnauthorizedException("Only administrators can view analytics across all companies");
            }
            
            summaries = repository.findBySummaryDateBetween(startDate, endDate);
        }

        return summaries.stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Get the latest summary for a specific company
     */
    @Transactional(readOnly = true)
    public AnalyticsDailySummaryDTO getLatestSummary(String companyExternalId) {
        if (companyExternalId == null) {
            // Only admins can get latest summary across all companies
            if (!securityService.isAdmin()) {
                throw new UnauthorizedException("Company external ID is required for non-admin users");
            }
            
            AnalyticsDailySummary summary = repository.findTopByOrderBySummaryDateDesc()
                    .orElseThrow(() -> new ResourceNotFoundException("No analytics summaries found"));
            
            return mapper.toDto(summary);
        }

        // Check if user has access to this company
        if (!securityService.isAdmin() &&
            !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
            throw new UnauthorizedException("You don't have permission to view this company's analytics");
        }

        AnalyticsDailySummary summary = repository.findTopByCompanyExternalIdOrderBySummaryDateDesc(companyExternalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AnalyticsDailySummary", 
                        "companyExternalId", 
                        companyExternalId));

        return mapper.toDto(summary);
    }

    /**
     * Create a new analytics summary (admin only)
     */
    @Transactional
    public AnalyticsDailySummaryDTO createSummary(AnalyticsDailySummaryDTO dto) {
        // Only admins can create analytics summaries
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create analytics summaries");
        }

        // Check for duplicate
        if (repository.existsByCompanyExternalIdAndSummaryDate(dto.getCompanyExternalId(), dto.getSummaryDate())) {
            throw new DuplicateResourceException(
                    "Analytics summary already exists for company " + 
                    dto.getCompanyExternalId() + " on date " + dto.getSummaryDate());
        }

        AnalyticsDailySummary summary = mapper.toEntity(dto);
        summary = repository.save(summary);
        
        log.info("Created analytics summary: id={}, companyExternalId={}, date={}", 
                summary.getId(), summary.getCompanyExternalId(), summary.getSummaryDate());
        
        return mapper.toDto(summary);
    }

    /**
     * Update an existing analytics summary (admin only)
     */
    @Transactional
    public AnalyticsDailySummaryDTO updateSummary(Long id, AnalyticsDailySummaryDTO dto) {
        // Only admins can update analytics summaries
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update analytics summaries");
        }

        AnalyticsDailySummary summary = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnalyticsDailySummary", "id", id));

        mapper.updateEntityFromDto(dto, summary);
        summary = repository.save(summary);
        
        log.info("Updated analytics summary: id={}, companyExternalId={}, date={}", 
                summary.getId(), summary.getCompanyExternalId(), summary.getSummaryDate());
        
        return mapper.toDto(summary);
    }

    /**
     * Delete an analytics summary (admin only, soft delete)
     */
    @Transactional
    public void deleteSummary(Long id) {
        // Only admins can delete analytics summaries
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete analytics summaries");
        }

        AnalyticsDailySummary summary = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnalyticsDailySummary", "id", id));

        repository.delete(summary); // Triggers soft delete via @SQLDelete
        
        log.info("Deleted analytics summary: id={}, companyExternalId={}, date={}", 
                id, summary.getCompanyExternalId(), summary.getSummaryDate());
    }

    /**
     * Check if summary exists by external ID
     */
    @Transactional(readOnly = true)
    public boolean existsByExternalId(String externalId) {
        return repository.existsByExternalId(externalId);
    }
}
