package org.clickenrent.searchservice.client;

import org.clickenrent.contracts.rental.BikeDTO;
import org.clickenrent.contracts.rental.HubDTO;
import org.clickenrent.contracts.rental.LocationDTO;
import org.clickenrent.searchservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for communicating with rental-service.
 * Used to fetch bike, location, and hub data for indexing and search.
 * 
 * @author Vitaliy Shvetsov
 */
@FeignClient(name = "rental-service", path = "/api/v1", configuration = FeignConfig.class)
public interface RentalServiceClient {

    // ==================== Bikes ====================
    
    /**
     * Get bike by external ID
     */
    @GetMapping("/bikes/external/{externalId}")
    BikeDTO getBikeByExternalId(@PathVariable("externalId") String externalId);

    /**
     * Get paginated list of bikes (for bulk indexing)
     */
    @GetMapping("/bikes")
    Page<BikeDTO> getBikes(
            @RequestParam(required = false) String companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    );

    // ==================== Locations ====================
    
    /**
     * Get location by external ID
     */
    @GetMapping("/location/external/{externalId}")
    LocationDTO getLocationByExternalId(@PathVariable("externalId") String externalId);

    /**
     * Get paginated list of locations (for bulk indexing)
     */
    @GetMapping("/location")
    Page<LocationDTO> getLocations(
            @RequestParam(required = false) String companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    );

    // ==================== Hubs ====================
    
    /**
     * Get hub by external ID
     */
    @GetMapping("/hubs/external/{externalId}")
    HubDTO getHubByExternalId(@PathVariable("externalId") String externalId);

    /**
     * Get paginated list of hubs (for bulk indexing)
     */
    @GetMapping("/hubs")
    Page<HubDTO> getHubs(
            @RequestParam(required = false) String companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    );
}
