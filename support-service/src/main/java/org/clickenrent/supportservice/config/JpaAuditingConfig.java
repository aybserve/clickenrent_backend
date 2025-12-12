package org.clickenrent.supportservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

/**
 * Configuration for JPA auditing.
 * Automatically tracks who created and modified entities.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("system");
            }
            
            // Extract username from JWT token
            if (authentication.getPrincipal() instanceof Jwt jwt) {
                String username = jwt.getClaim("sub");
                return Optional.ofNullable(username);
            }
            
            return Optional.of(authentication.getName());
        };
    }
}
