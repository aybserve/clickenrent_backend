package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for Swagger access validation response.
 * Indicates whether user has access to Swagger and their roles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwaggerAccessResponse {
    
    /**
     * Whether the user has access to Swagger documentation.
     * True if user has SUPERADMIN, ADMIN, or DEV role.
     */
    private boolean hasAccess;
    
    /**
     * List of user's global roles.
     */
    private List<String> roles;
    
    /**
     * Username of the authenticated user.
     */
    private String username;
}
