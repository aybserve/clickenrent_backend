package org.clickenrent.rentalservice.client;

import org.clickenrent.rentalservice.dto.CompanyDTO;
import org.clickenrent.rentalservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with Auth Service.
 * Used to fetch user and company details.
 */
@FeignClient(name = "auth-service", path = "/api")
public interface AuthServiceClient {

    /**
     * Get user by ID from auth-service
     */
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

    /**
     * Get company by ID from auth-service
     */
    @GetMapping("/companies/{id}")
    CompanyDTO getCompanyById(@PathVariable("id") Long id);
}


