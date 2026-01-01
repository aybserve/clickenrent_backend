package org.clickenrent.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.SwaggerAccessRequest;
import org.clickenrent.authservice.dto.SwaggerAccessResponse;
import org.clickenrent.authservice.service.SwaggerAccessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for validating Swagger access permissions.
 * Used by Gateway to validate user credentials for Swagger UI access.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Swagger Access", description = "Endpoints for validating Swagger documentation access")
public class SwaggerAccessController {
    
    private final SwaggerAccessService swaggerAccessService;
    
    /**
     * Validate user credentials and check Swagger access permissions.
     * This endpoint is public and used by Gateway for HTTP Basic Auth validation.
     * 
     * @param request SwaggerAccessRequest containing username/email and password
     * @return SwaggerAccessResponse indicating access status and roles
     */
    @PostMapping("/validate-swagger-access")
    @Operation(
        summary = "Validate Swagger access",
        description = "Validates user credentials and checks if they have SUPERADMIN, ADMIN, or DEV role for Swagger access"
    )
    public ResponseEntity<SwaggerAccessResponse> validateSwaggerAccess(
            @Valid @RequestBody SwaggerAccessRequest request) {
        
        SwaggerAccessResponse response = swaggerAccessService.validateSwaggerAccess(request);
        return ResponseEntity.ok(response);
    }
}
