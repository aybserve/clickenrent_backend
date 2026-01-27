package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.search.IndexEventRequest;
import org.clickenrent.rentalservice.client.SearchServiceClient;
import org.clickenrent.rentalservice.dto.BikeDTO;
import org.clickenrent.rentalservice.dto.BikeLocationDTO;
import org.clickenrent.rentalservice.dto.GeoPointDTO;
import org.clickenrent.rentalservice.dto.NearbyBikesResponseDTO;
import org.clickenrent.rentalservice.entity.Bike;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeMapper;
import org.clickenrent.rentalservice.repository.BikeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Bike entities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BikeService {

    private final BikeRepository bikeRepository;
    private final BikeMapper bikeMapper;
    private final SecurityService securityService;
    private final LocationCalculationService locationCalculationService;
    private final SearchServiceClient searchServiceClient;

    @Transactional(readOnly = true)
    public Page<BikeDTO> getAllBikes(Pageable pageable) {
        // Admin can see all bikes
        if (securityService.isAdmin()) {
            return bikeRepository.findAll(pageable)
                    .map(bikeMapper::toDto);
        }

        // B2B/Customer see bikes based on their company access
        throw new UnauthorizedException("You don't have permission to view all bikes");
    }

    @Transactional(readOnly = true)
    public BikeDTO getBikeById(Long id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "id", id));
        return bikeMapper.toDto(bike);
    }

    /**
     * Find bike by externalId for cross-service communication
     */
    @Transactional(readOnly = true)
    public BikeDTO findByExternalId(String externalId) {
        Bike bike = bikeRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "externalId", externalId));
        return bikeMapper.toDto(bike);
    }

    @Transactional(readOnly = true)
    public BikeDTO getBikeByCode(String code) {
        Bike bike = bikeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "code", code));
        return bikeMapper.toDto(bike);
    }

    @Transactional
    public BikeDTO createBike(BikeDTO bikeDTO) {
        // Only admins can create bikes
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bikes");
        }

        Bike bike = bikeMapper.toEntity(bikeDTO);
        bike = bikeRepository.save(bike);
        
        // Notify search-service for indexing
        notifySearchService("bike", bike.getExternalId(), "CREATE");
        
        return bikeMapper.toDto(bike);
    }

    @Transactional
    public BikeDTO updateBike(Long id, BikeDTO bikeDTO) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "id", id));

        // Only admins can update bikes
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bikes");
        }

        bikeMapper.updateEntityFromDto(bikeDTO, bike);
        bike = bikeRepository.save(bike);
        
        // Notify search-service for indexing
        notifySearchService("bike", bike.getExternalId(), "UPDATE");
        
        return bikeMapper.toDto(bike);
    }

    @Transactional
    public BikeDTO updateByExternalId(String externalId, BikeDTO dto) {
        Bike bike = bikeRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "externalId", externalId));
        
        // Only admins can update bikes
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bikes");
        }
        
        // Update fields (use existing pattern from ID-based update)
        bikeMapper.updateEntityFromDto(dto, bike);
        bike = bikeRepository.save(bike);
        log.info("Updated bike by externalId: {}", externalId);
        
        // Notify search-service for indexing
        notifySearchService("bike", bike.getExternalId(), "UPDATE");
        
        return bikeMapper.toDto(bike);
    }

    @Transactional
    public void deleteBike(Long id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "id", id));

        // Only admins can delete bikes
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bikes");
        }

        String externalId = bike.getExternalId();
        bikeRepository.delete(bike);
        
        // Notify search-service for indexing
        notifySearchService("bike", externalId, "DELETE");
    }

    @Transactional
    public void deleteByExternalId(String externalId) {
        Bike bike = bikeRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Bike", "externalId", externalId));
        
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete bikes");
        }
        
        bikeRepository.delete(bike);
        log.info("Deleted bike by externalId: {}", externalId);
        
        // Notify search-service for indexing
        notifySearchService("bike", externalId, "DELETE");
    }

    /**
     * Find bikes nearby a given location within a specified radius.
     * 
     * @param latitude Center point latitude
     * @param longitude Center point longitude
     * @param radiusKm Search radius in kilometers
     * @param limit Maximum number of results (default 50)
     * @param bikeStatusId Optional bike status filter
     * @return Response with nearby bikes and total count
     */
    @Transactional(readOnly = true)
    public NearbyBikesResponseDTO findNearbyBikes(
            Double latitude,
            Double longitude,
            Double radiusKm,
            Integer limit,
            Long bikeStatusId) {
        
        // Validate coordinates
        if (!locationCalculationService.isValidCoordinates(latitude, longitude)) {
            throw new IllegalArgumentException("Invalid coordinates: latitude must be between -90 and 90, longitude between -180 and 180");
        }

        // Validate radius
        if (radiusKm == null || radiusKm <= 0 || radiusKm > 100) {
            throw new IllegalArgumentException("Radius must be between 0 and 100 km");
        }

        // Set default limit if not provided
        if (limit == null || limit <= 0) {
            limit = 50;
        }

        // Cap limit at 200 to prevent abuse
        if (limit > 200) {
            limit = 200;
        }

        // Convert radius from km to meters for PostGIS
        Double radiusMeters = radiusKm * 1000;

        log.info("Finding bikes near {},{} within {} km (limit: {})", latitude, longitude, radiusKm, limit);

        // Query bikes using PostGIS spatial query
        List<Object[]> results;
        if (bikeStatusId != null) {
            results = bikeRepository.findNearbyBikesByStatus(latitude, longitude, radiusMeters, bikeStatusId, limit);
        } else {
            results = bikeRepository.findNearbyBikes(latitude, longitude, radiusMeters, limit);
        }

        // Get total count
        Long total = bikeRepository.countNearbyBikes(latitude, longitude, radiusMeters);

        // Map results to DTOs
        List<BikeLocationDTO> bikes = results.stream()
                .map(this::mapToBikeLocationDTO)
                .collect(Collectors.toList());

        log.info("Found {} bikes nearby (total: {})", bikes.size(), total);

        return NearbyBikesResponseDTO.builder()
                .bikes(bikes)
                .total(total)
                .build();
    }

    /**
     * Map database result array to BikeLocationDTO.
     * 
     * @param result Object array from native query
     * @return BikeLocationDTO
     */
    private BikeLocationDTO mapToBikeLocationDTO(Object[] result) {
        int i = 0;
        
        // Extract values from result array
        Long id = result[i++] != null ? ((Number) result[i - 1]).longValue() : null;
        String externalId = (String) result[i++];
        String code = (String) result[i++];
        String name = (String) result[i++];
        Long bikeStatusId = result[i++] != null ? ((Number) result[i - 1]).longValue() : null;
        String bikeStatusName = (String) result[i++];
        Integer batteryLevel = result[i++] != null ? ((Number) result[i - 1]).intValue() : null;
        BigDecimal latitude = (BigDecimal) result[i++];
        BigDecimal longitude = (BigDecimal) result[i++];
        Double distanceMeters = result[i++] != null ? ((Number) result[i - 1]).doubleValue() : null;
        String hubExternalId = (String) result[i++];
        String hubName = (String) result[i++];

        // Convert distance from meters to kilometers and round to 2 decimal places
        Double distanceKm = distanceMeters != null 
                ? locationCalculationService.roundDistance(distanceMeters / 1000.0, 2) 
                : null;

        return BikeLocationDTO.builder()
                .id(externalId != null ? externalId : (id != null ? id.toString() : null))
                .code(code)
                .name(name)
                .bikeStatus(bikeStatusId)
                .bikeStatusName(bikeStatusName)
                .batteryLevel(batteryLevel)
                .location(GeoPointDTO.builder()
                        .latitude(latitude)
                        .longitude(longitude)
                        .build())
                .distance(distanceKm)
                .distanceUnit("km")
                .hubExternalId(hubExternalId)
                .hubName(hubName)
                .build();
    }
    
    /**
     * Notify search-service of entity changes (async, fail-safe)
     * This method never throws exceptions to prevent search failures from breaking bike operations
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








