package org.clickenrent.rentalservice.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.search.IndexEventRequest;
import org.clickenrent.contracts.security.AuditEvent;
import org.clickenrent.contracts.security.AuditService;
import org.clickenrent.rentalservice.client.SearchServiceClient;
import org.clickenrent.rentalservice.event.IndexEventPublisher;
import org.clickenrent.rentalservice.dto.LocationDTO;
import org.clickenrent.rentalservice.entity.Hub;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.LocationMapper;
import org.clickenrent.rentalservice.repository.HubRepository;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Service for managing Location entities with automatic hub creation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;
    private final HubRepository hubRepository;
    private final LocationMapper locationMapper;
    private final SecurityService securityService;
    private final SearchServiceClient searchServiceClient;
    private final AuditService auditService;

    @Autowired(required = false)
    private IndexEventPublisher indexEventPublisher;

    @Transactional(readOnly = true)
    public Page<LocationDTO> getAllLocations(Pageable pageable) {
        // Admin can see all locations
        if (securityService.isAdmin()) {
            return locationRepository.findAll(pageable)
                    .map(locationMapper::toDto);
        }

        // B2B/Customer can see locations of their companies
        // Implementation depends on user's company access
        throw new UnauthorizedException("You don't have permission to view all locations");
    }

    @Transactional(readOnly = true)
    public Page<LocationDTO> getAllLocations(String companyId, int page, int size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        
        // If companyId is specified, filter by it
        if (companyId != null && !companyId.isBlank()) {
            return locationRepository.findByCompanyExternalId(companyId, pageable)
                    .map(locationMapper::toDto);
        }
        
        // Otherwise, use the existing method
        return getAllLocations(pageable);
    }

    @Transactional(readOnly = true)
    public LocationDTO getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", id));

        // Check if user has access to this location's company
        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(location.getCompanyExternalId())) {
            logLocationAccessDenied(id, location);
            throw new UnauthorizedException("You don't have permission to view this location");
        }

        return locationMapper.toDto(location);
    }

    /**
     * Logs audit event when GET /api/v1/location/{id} is denied due to company access.
     */
    private void logLocationAccessDenied(Long locationId, Location location) {
        try {
            HttpServletRequest request = null;
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes) {
                request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            }
            String userExternalId = securityService.getCurrentUserExternalId();
            String userCompanyIds = String.join(",", securityService.getCurrentUserCompanyExternalIds());

            AuditEvent event = AuditEvent.builder()
                    .eventType(AuditEvent.EventType.CROSS_TENANT_ACCESS_ATTEMPT)
                    .userExternalId(userExternalId)
                    .userCompanyIds(userCompanyIds)
                    .attemptedCompanyId(location.getCompanyExternalId())
                    .resourceType("Location")
                    .resourceId(location.getExternalId() != null ? location.getExternalId() : String.valueOf(locationId))
                    .endpoint(request != null ? request.getRequestURI() : "/api/v1/location/" + locationId)
                    .httpMethod("GET")
                    .clientIp(request != null ? request.getRemoteAddr() : null)
                    .success(false)
                    .allowed(false)
                    .errorMessage("You don't have permission to view this location")
                    .message(String.format("User (companies: %s) attempted to access Location id=%s from company %s",
                            userCompanyIds.isEmpty() ? "none" : userCompanyIds, locationId, location.getCompanyExternalId()))
                    .build();

            auditService.logEvent(event);
        } catch (Exception e) {
            log.warn("Failed to log audit event for location access denied: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public LocationDTO getLocationByExternalId(String externalId) {
        Location location = locationRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "externalId", externalId));
        return locationMapper.toDto(location);
    }

    @Transactional
    public LocationDTO createLocation(LocationDTO locationDTO) {
        // Check if user has access to the company
        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(locationDTO.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to create location for this company");
        }

        Location location = locationMapper.toEntity(locationDTO);
        location.sanitizeForCreate();
        location = locationRepository.save(location);

        // Auto-create "Main" hub for this location
        Hub mainHub = Hub.builder()
                .name("Main")
                .location(location)
                .build();
        hubRepository.save(mainHub);

        // Notify search-service for indexing
        notifySearchService("location", location.getExternalId(), "CREATE");

        return locationMapper.toDto(location);
    }

    @Transactional
    public LocationDTO updateLocation(Long id, LocationDTO locationDTO) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", id));

        // Check if user has access to this location's company
        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(location.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to update this location");
        }

        locationMapper.updateEntityFromDto(locationDTO, location);
        location = locationRepository.save(location);
        
        // Notify search-service for indexing
        notifySearchService("location", location.getExternalId(), "UPDATE");
        
        return locationMapper.toDto(location);
    }

    @Transactional
    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", id));

        // Check if user has access to this location's company
        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(location.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to delete this location");
        }

        String externalId = location.getExternalId();
        locationRepository.delete(location);
        
        // Notify search-service for indexing
        notifySearchService("location", externalId, "DELETE");
    }

    @Transactional(readOnly = true)
    public boolean existsByExternalId(String externalId) {
        return locationRepository.existsByExternalId(externalId);
    }
    
    /**
     * Notify search-service of entity changes (async, fail-safe)
     * This method never throws exceptions to prevent search failures from breaking location operations
     */
    private void notifySearchService(String entityType, String entityId, String operation) {
        try {
            if (indexEventPublisher != null) {
                // New: Kafka event-driven approach
                indexEventPublisher.publishIndexEvent(
                        entityType,
                        entityId,
                        IndexEventRequest.IndexOperation.valueOf(operation)
                );
                log.debug("Published index event: {} {} {}", operation, entityType, entityId);
            } else {
                // Fallback: Direct Feign call
                searchServiceClient.notifyIndexEvent(
                    IndexEventRequest.builder()
                        .entityType(entityType)
                        .entityId(entityId)
                        .operation(IndexEventRequest.IndexOperation.valueOf(operation))
                        .build()
                );
                log.debug("Notified search-service via Feign: {} {} {}", operation, entityType, entityId);
            }
        } catch (Exception e) {
            // Don't fail the main operation if search indexing fails
            log.warn("Failed to notify search-service for {} {} {}: {}", 
                     operation, entityType, entityId, e.getMessage());
        }
    }
}




