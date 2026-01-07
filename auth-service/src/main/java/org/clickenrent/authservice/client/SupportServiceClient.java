package org.clickenrent.authservice.client;

import org.clickenrent.authservice.config.FeignConfig;
import org.clickenrent.contracts.support.BikeRentalFeedbackDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign client for communicating with Support Service.
 * Used to fetch bike rental feedback data for statistics.
 */
@FeignClient(name = "support-service", path = "/api/v1", configuration = FeignConfig.class)
public interface SupportServiceClient {

    /**
     * Get bike rental feedbacks by user external ID
     */
    @GetMapping("/bike-rental-feedbacks/user/{userExternalId}")
    List<BikeRentalFeedbackDTO> getByUserExternalId(@PathVariable("userExternalId") String userExternalId);
}

