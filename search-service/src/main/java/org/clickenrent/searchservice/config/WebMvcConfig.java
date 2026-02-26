package org.clickenrent.searchservice.config;

import lombok.RequiredArgsConstructor;
import org.clickenrent.searchservice.security.TenantInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for search-service.
 * Registers the TenantInterceptor to extract tenant context from every request.
 * 
 * @author Vitaliy Shvetsov
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final TenantInterceptor tenantInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/actuator/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/health"
                );
    }
}
