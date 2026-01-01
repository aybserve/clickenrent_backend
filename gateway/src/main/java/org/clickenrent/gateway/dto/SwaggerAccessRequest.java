package org.clickenrent.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for requesting Swagger access validation from auth-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwaggerAccessRequest {
    private String usernameOrEmail;
    private String password;
}
