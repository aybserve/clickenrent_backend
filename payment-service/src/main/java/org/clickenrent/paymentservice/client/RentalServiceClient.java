package org.clickenrent.paymentservice.client;

import org.clickenrent.contracts.rental.BikeDTO;
import org.clickenrent.contracts.rental.BikeRentalDTO;
import org.clickenrent.contracts.rental.RentalDTO;
import org.clickenrent.paymentservice.dto.BikeRentalPayoutDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * Feign client for communicating with Rental Service.
 * Used to validate rental, bike rental, B2B sale, and B2B subscription references.
 */
@FeignClient(name = "rental-service", path = "/api/v1")
public interface RentalServiceClient {

    /**
     * Get rental by ID
     */
    @GetMapping("/rentals/{id}")
    RentalDTO getRentalById(@PathVariable("id") Long rentalId);

    /**
     * Check if rental exists by ID
     */
    @GetMapping("/rentals/{id}/exists")
    Boolean checkRentalExists(@PathVariable("id") Long rentalId);

    /**
     * Get bike rental by ID
     */
    @GetMapping("/bike-rentals/{id}")
    BikeRentalDTO getBikeRentalById(@PathVariable("id") Long bikeRentalId);

    /**
     * Get bike rental by external ID (for cross-service communication)
     */
    @GetMapping("/bike-rentals/external/{externalId}")
    BikeRentalDTO getBikeRentalByExternalId(@PathVariable("externalId") String externalId);

    /**
     * Check if bike rental exists by ID
     */
    @GetMapping("/bike-rentals/{id}/exists")
    Boolean checkBikeRentalExists(@PathVariable("id") Long bikeRentalId);

    /**
     * Check if B2B sale exists by ID
     */
    @GetMapping("/b2b-sales/{id}/exists")
    Boolean checkB2BSaleExists(@PathVariable("id") Long saleId);

    /**
     * Check if B2B subscription exists by ID
     */
    @GetMapping("/b2b-subscriptions/{id}/exists")
    Boolean checkB2BSubscriptionExists(@PathVariable("id") Long subscriptionId);

    /**
     * Get rental by external ID (for cross-service communication)
     */
    @GetMapping("/rentals/external/{externalId}")
    RentalDTO getRentalByExternalId(@PathVariable("externalId") String externalId);

    /**
     * Check if rental exists by external ID
     */
    @GetMapping("/rentals/external/{externalId}/exists")
    Boolean checkRentalExistsByExternalId(@PathVariable("externalId") String externalId);

    /**
     * Get bike by external ID (for cross-service communication)
     */
    @GetMapping("/bikes/external/{externalId}")
    BikeDTO getBikeByExternalId(@PathVariable("externalId") String externalId);

    // ========== PAYOUT-RELATED METHODS ==========

    /**
     * Get all unpaid bike rentals within a date range for payout processing
     * 
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of bike rentals that haven't been paid out yet
     */
    @GetMapping("/bike-rentals/unpaid")
    List<BikeRentalPayoutDTO> getUnpaidBikeRentals(
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    );
    
    /**
     * Mark bike rentals as paid after successful payout
     * 
     * @param bikeRentalExternalIds List of bike rental external IDs to mark as paid
     */
    @PostMapping("/bike-rentals/mark-paid")
    void markBikeRentalsAsPaid(@RequestBody List<String> bikeRentalExternalIds);
}




