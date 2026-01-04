package org.clickenrent.authservice.client;

import org.clickenrent.authservice.config.FeignConfig;
import org.clickenrent.contracts.rental.BikeRentalDTO;
import org.clickenrent.contracts.rental.LocationDTO;
import org.clickenrent.contracts.rental.RentalDTO;
import org.clickenrent.contracts.rental.RideDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign client for communicating with Rental Service.
 * Used to fetch rental, bike rental, and ride data for statistics.
 */
@FeignClient(name = "rental-service", path = "/api", configuration = FeignConfig.class)
public interface RentalServiceClient {

    /**
     * Get rentals by user external ID
     */
    @GetMapping("/rentals/user/{userExternalId}")
    List<RentalDTO> getRentalsByUserExternalId(@PathVariable("userExternalId") String userExternalId);

    /**
     * Get bike rentals by rental external ID
     */
    @GetMapping("/bike-rentals/rental/external/{rentalExternalId}")
    List<BikeRentalDTO> getBikeRentalsByRentalExternalId(@PathVariable("rentalExternalId") String rentalExternalId);

    /**
     * Get rides by bike rental external ID
     */
    @GetMapping("/rides/by-bike-rental/external/{bikeRentalExternalId}")
    List<RideDTO> getRidesByBikeRentalExternalId(@PathVariable("bikeRentalExternalId") String bikeRentalExternalId);

    /**
     * Get location by ID
     */
    @GetMapping("/locations/{id}")
    LocationDTO getLocationById(@PathVariable("id") Long id);
}

