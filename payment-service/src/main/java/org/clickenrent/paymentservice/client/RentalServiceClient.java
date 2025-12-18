package org.clickenrent.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with Rental Service.
 * Used to validate rental, bike rental, B2B sale, and B2B subscription references.
 */
@FeignClient(name = "rental-service", path = "/api")
public interface RentalServiceClient {

    /**
     * Check if rental exists by ID
     */
    @GetMapping("/rentals/{id}/exists")
    Boolean checkRentalExists(@PathVariable("id") Long rentalId);

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
}


