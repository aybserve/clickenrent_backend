package org.clickenrent.supportservice.client;

import org.clickenrent.contracts.rental.BikeDTO;
import org.clickenrent.contracts.rental.BikeTypeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with Rental Service to fetch bike details.
 * Used to validate bike references and populate bike external IDs.
 */
@FeignClient(name = "rental-service", path = "/api/v1")
public interface BikeServiceClient {

    /**
     * Get bike by ID from rental-service
     */
    @GetMapping("/bikes/{id}")
    BikeDTO getBikeById(@PathVariable("id") Long id);

    /**
     * Get bike by external ID from rental-service (for cross-service communication)
     */
    @GetMapping("/bikes/external/{externalId}")
    BikeDTO getBikeByExternalId(@PathVariable("externalId") String externalId);

    /**
     * Get bike type by external ID from rental-service (for cross-service communication)
     */
    @GetMapping("/bike-types/external/{externalId}")
    BikeTypeDTO getBikeTypeByExternalId(@PathVariable("externalId") String externalId);
}







