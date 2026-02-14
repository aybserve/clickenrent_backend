package org.clickenrent.gateway.config;

import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger/OpenAPI configuration for Gateway.
 * Aggregates API documentation from all microservices.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configure Swagger UI with service definitions from routes.
     */
    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiConfigParameters) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        
        // Add all microservices explicitly for Swagger UI aggregation
        swaggerUiConfigParameters.addGroup("auth-service");
        swaggerUiConfigParameters.addGroup("rental-service");
        swaggerUiConfigParameters.addGroup("support-service");
        swaggerUiConfigParameters.addGroup("notification-service");
        swaggerUiConfigParameters.addGroup("payment-service");
        swaggerUiConfigParameters.addGroup("search-service");
        swaggerUiConfigParameters.addGroup("analytics-service");
        
        return groups;
    }
    
    /**
     * Customize OpenAPI to use dynamic server URL based on the request.
     * This ensures Swagger UI uses the correct host (staging.api.clickenrent.nl)
     * instead of hardcoded localhost:8080.
     */
    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // Clear any existing servers
            openApi.setServers(new ArrayList<>());
            
            // Add relative URL server (will use current host)
            Server server = new Server();
            server.setUrl("/");
            server.setDescription("Current server");
            openApi.addServersItem(server);
        };
    }
}

