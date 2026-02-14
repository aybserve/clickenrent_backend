package org.clickenrent.analyticsservice.client;

import org.clickenrent.analyticsservice.dto.LanguageDTO;
import org.clickenrent.analyticsservice.dto.UserPageDTO;
import org.clickenrent.contracts.auth.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client for communicating with auth-service.
 * Used to fetch user data for analytics.
 */
@FeignClient(
    name = "auth-service",
    path = "/api/v1"
)
public interface AuthServiceClient {

    /**
     * Get all users for analytics.
     * The auth-service will automatically filter by the user's companies via security context.
     *
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @return Page of users
     */
    @GetMapping("/users")
    UserPageDTO getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size
    );

    /**
     * Get user by external ID.
     * Used to check if a user is active for cross-service communication.
     *
     * @param externalId User external ID
     * @return User details including active status
     */
    @GetMapping("/users/external/{externalId}")
    UserDTO getUserByExternalId(@PathVariable("externalId") String externalId);

    /**
     * Get all languages from auth-service.
     * Used to map language IDs to names for analytics.
     *
     * @return List of languages
     */
    @GetMapping("/languages")
    List<LanguageDTO> getAllLanguages();
}
