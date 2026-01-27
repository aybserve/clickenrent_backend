package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.search.IndexEventRequest;
import org.clickenrent.rentalservice.client.SearchServiceClient;
import org.clickenrent.rentalservice.dto.HubDTO;
import org.clickenrent.rentalservice.entity.Hub;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.HubMapper;
import org.clickenrent.rentalservice.repository.HubRepository;
import org.clickenrent.rentalservice.repository.LocationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing Hub entities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HubService {

    private final HubRepository hubRepository;
    private final LocationRepository locationRepository;
    private final HubMapper hubMapper;
    private final SecurityService securityService;
    private final SearchServiceClient searchServiceClient;

    @Transactional(readOnly = true)
    public Page<HubDTO> getAllHubs(Pageable pageable) {
        if (securityService.isAdmin()) {
            return hubRepository.findAll(pageable)
                    .map(hubMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all hubs");
    }

    @Transactional(readOnly = true)
    public List<HubDTO> getHubsByLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", locationId));

        // Check access to location's company
        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(location.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to view hubs for this location");
        }

        return hubRepository.findByLocation(location).stream()
                .map(hubMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public HubDTO getHubById(Long id) {
        Hub hub = hubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hub", "id", id));

        // Check access to hub's location company
        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(hub.getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to view this hub");
        }

        return hubMapper.toDto(hub);
    }

    @Transactional
    public HubDTO createHub(HubDTO hubDTO) {
        // Validate location exists and user has access
        Location location = locationRepository.findById(hubDTO.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", hubDTO.getLocationId()));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(location.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to create hub for this location");
        }

        Hub hub = hubMapper.toEntity(hubDTO);
        hub = hubRepository.save(hub);
        
        // Notify search-service for indexing
        notifySearchService("hub", hub.getExternalId(), "CREATE");
        
        return hubMapper.toDto(hub);
    }

    @Transactional
    public HubDTO updateHub(Long id, HubDTO hubDTO) {
        Hub hub = hubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hub", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(hub.getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to update this hub");
        }

        hubMapper.updateEntityFromDto(hubDTO, hub);
        hub = hubRepository.save(hub);
        
        // Notify search-service for indexing
        notifySearchService("hub", hub.getExternalId(), "UPDATE");
        
        return hubMapper.toDto(hub);
    }

    @Transactional
    public void deleteHub(Long id) {
        Hub hub = hubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hub", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(hub.getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to delete this hub");
        }

        String externalId = hub.getExternalId();
        hubRepository.delete(hub);
        
        // Notify search-service for indexing
        notifySearchService("hub", externalId, "DELETE");
    }

    @Transactional(readOnly = true)
    public HubDTO getHubByExternalId(String externalId) {
        Hub hub = hubRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hub", "externalId", externalId));
        return hubMapper.toDto(hub);
    }
    
    /**
     * Notify search-service of entity changes (async, fail-safe)
     * This method never throws exceptions to prevent search failures from breaking hub operations
     */
    private void notifySearchService(String entityType, String entityId, String operation) {
        try {
            searchServiceClient.notifyIndexEvent(
                IndexEventRequest.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .operation(IndexEventRequest.IndexOperation.valueOf(operation))
                    .build()
            );
            log.debug("Notified search-service: {} {} {}", operation, entityType, entityId);
        } catch (Exception e) {
            // Don't fail the main operation if search indexing fails
            log.warn("Failed to notify search-service for {} {} {}: {}", 
                     operation, entityType, entityId, e.getMessage());
        }
    }
}




