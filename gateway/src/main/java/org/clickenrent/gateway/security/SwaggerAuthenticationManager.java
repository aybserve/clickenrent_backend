package org.clickenrent.gateway.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.gateway.dto.SwaggerAccessRequest;
import org.clickenrent.gateway.dto.SwaggerAccessResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom authentication manager for Swagger Basic Auth.
 * Validates credentials against auth-service and caches results.
 */
@Component
@Slf4j
public class SwaggerAuthenticationManager implements ReactiveAuthenticationManager {
    
    private final WebClient webClient;
    private final Cache<String, SwaggerAccessResponse> authCache;
    
    public SwaggerAuthenticationManager(
            @Value("${auth-service.url:http://localhost:8081}") String authServiceUrl,
            WebClient.Builder webClientBuilder) {
        
        log.info("Initializing SwaggerAuthenticationManager with auth-service URL: {}", authServiceUrl);
        
        this.webClient = webClientBuilder
                .baseUrl(authServiceUrl)
                .build();
        
        // Cache authentication results for 5 minutes
        this.authCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(5))
                .build();
    }
    
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        log.debug("Authenticating user '{}' for Swagger access", username);
        
        // Create cache key
        String cacheKey = username + ":" + password.hashCode();
        
        // Check cache first
        SwaggerAccessResponse cachedResponse = authCache.getIfPresent(cacheKey);
        if (cachedResponse != null) {
            log.debug("Using cached authentication for user: {}", username);
            return createAuthentication(cachedResponse, password);
        }
        
        // Validate with auth-service
        log.debug("Calling auth-service to validate credentials for user: {}", username);
        SwaggerAccessRequest request = SwaggerAccessRequest.builder()
                .usernameOrEmail(username)
                .password(password)
                .build();
        
        return webClient.post()
                .uri("/api/v1/auth/validate-swagger-access")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SwaggerAccessResponse.class)
                .flatMap(response -> {
                    if (response.isHasAccess()) {
                        // Cache successful authentication
                        authCache.put(cacheKey, response);
                        log.info("User '{}' authenticated successfully for Swagger access with roles: {}", 
                                username, response.getRoles());
                        return createAuthentication(response, password);
                    } else {
                        log.warn("User '{}' denied Swagger access - insufficient permissions", username);
                        return Mono.error(new BadCredentialsException("Access denied - insufficient permissions"));
                    }
                })
                .onErrorResume(error -> {
                    log.error("Error validating Swagger access for user '{}': {}", username, error.getMessage());
                    return Mono.error(new BadCredentialsException("Authentication failed: " + error.getMessage()));
                });
    }
    
    private Mono<Authentication> createAuthentication(SwaggerAccessResponse response, String password) {
        List<SimpleGrantedAuthority> authorities = response.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
        
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                response.getUsername(),
                password,
                authorities
        );
        
        return Mono.just(auth);
    }
}
