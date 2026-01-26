package org.clickenrent.searchservice.client;

import org.clickenrent.contracts.auth.UserDTO;
import org.clickenrent.searchservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for communicating with auth-service.
 * Used to fetch user data for indexing and search.
 * 
 * @author Vitaliy Shvetsov
 */
@FeignClient(name = "auth-service", path = "/api/v1", configuration = FeignConfig.class)
public interface AuthServiceClient {

    /**
     * Get user by external ID
     */
    @GetMapping("/users/external/{externalId}")
    UserDTO getUserByExternalId(@PathVariable("externalId") String externalId);

    /**
     * Get paginated list of users (for bulk indexing)
     */
    @GetMapping("/users")
    Page<UserDTO> getUsers(
            @RequestParam(required = false) String companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    );
}
