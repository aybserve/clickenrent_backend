package org.clickenrent.analyticsservice.client;

import org.clickenrent.analyticsservice.dto.UserPageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
