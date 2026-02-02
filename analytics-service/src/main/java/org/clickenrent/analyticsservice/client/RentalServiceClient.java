package org.clickenrent.analyticsservice.client;

import org.clickenrent.analyticsservice.dto.BikePageDTO;
import org.clickenrent.analyticsservice.dto.BikeRentalPageDTO;
import org.clickenrent.analyticsservice.dto.LocationPageDTO;
import org.clickenrent.analyticsservice.dto.RideSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * Feign client for communicating with rental-service.
 * Used to fetch real-time bike rental, fleet, and location data for analytics.
 */
@FeignClient(
    name = "rental-service",
    path = "/api/v1"
)
public interface RentalServiceClient {

    /**
     * Get bike rentals with pagination and date filtering.
     * The rental-service will automatically filter by the user's company via security context.
     *
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param startDate Filter rentals from this date (optional)
     * @param endDate Filter rentals to this date (optional)
     * @return Page of bike rentals
     */
    @GetMapping("/bike-rentals")
    BikeRentalPageDTO getBikeRentals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    );

    /**
     * Get rides for a specific bike rental.
     *
     * @param bikeRentalExternalId External ID of the bike rental
     * @return List of rides
     */
    @GetMapping("/rides/by-bike-rental/external/{bikeRentalExternalId}")
    List<RideSummaryDTO> getRidesByBikeRentalExternalId(
            @PathVariable String bikeRentalExternalId
    );

    /**
     * Get bikes with pagination.
     * The rental-service will automatically filter by the user's company via security context.
     *
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Page of bikes
     */
    @GetMapping("/bikes")
    BikePageDTO getBikes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size
    );

    /**
     * Get locations with pagination.
     * The rental-service will automatically filter by the user's company via security context.
     *
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Page of locations
     */
    @GetMapping("/location")
    LocationPageDTO getLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size
    );
}
