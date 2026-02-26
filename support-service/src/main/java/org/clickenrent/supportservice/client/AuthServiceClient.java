package org.clickenrent.supportservice.client;

import org.clickenrent.contracts.auth.CompanyDTO;
import org.clickenrent.contracts.auth.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with Auth Service.
 * Used to fetch user and company details.
 */
@FeignClient(name = "auth-service", path = "/api/v1")
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

    /**
     * Get user by external ID from auth-service (for cross-service communication)
     */
    @GetMapping("/users/external/{externalId}")
    UserDTO getUserByExternalId(@PathVariable("externalId") String externalId);

    /**
     * Get company by external ID from auth-service (for cross-service communication)
     */
    @GetMapping("/companies/external/{externalId}")
    CompanyDTO getCompanyByExternalId(@PathVariable("externalId") String externalId);
}








