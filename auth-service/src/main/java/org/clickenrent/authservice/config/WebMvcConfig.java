package org.clickenrent.authservice.config;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.security.TenantInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for auth-service.
 * Registers the TenantInterceptor to run before all API requests.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final TenantInterceptor tenantInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/register", "/api/auth/refresh", 
                                     "/api/invitations/validate/**", "/api/invitations/complete/**",
                                     "/api/health", "/api/actuator/**");
    }
}
