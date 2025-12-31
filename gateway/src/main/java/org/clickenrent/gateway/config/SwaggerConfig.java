package org.clickenrent.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.server.ServerWebExchange;

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
    public List<GroupedOpenApi> apis(RouteDefinitionLocator locator, SwaggerUiConfigParameters swaggerUiConfigParameters) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        
        // Add auth-service explicitly
        swaggerUiConfigParameters.addGroup("auth-service");
        
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

