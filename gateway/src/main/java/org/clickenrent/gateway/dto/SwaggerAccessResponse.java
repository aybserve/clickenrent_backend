package org.clickenrent.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for Swagger access validation response from auth-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwaggerAccessResponse {
    private boolean hasAccess;
    private List<String> roles;
    private String username;
}
