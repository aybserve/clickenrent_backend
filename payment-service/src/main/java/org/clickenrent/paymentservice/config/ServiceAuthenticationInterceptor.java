package org.clickenrent.paymentservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Feign request interceptor for service-to-service authentication.
 * Authenticates as a service account and adds JWT token to outgoing requests.
 */
@Slf4j
@Component
public class ServiceAuthenticationInterceptor implements RequestInterceptor {

    @Value("${service.auth.username:service_payment}")
    private String serviceUsername;

    @Value("${service.auth.password:password}")
    private String servicePassword;

    @Value("${service.auth.url:http://auth-service/api/v1/auth/login}")
    private String authUrl;

    private String cachedToken;
    private long tokenExpiryTime;
    private final RestTemplate restTemplate;

    public ServiceAuthenticationInterceptor(RestTemplate loadBalancedRestTemplate) {
        this.restTemplate = loadBalancedRestTemplate;
    }

    @Override
    public void apply(RequestTemplate template) {
        String token = getServiceToken();
        if (token != null) {
            template.header("Authorization", "Bearer " + token);
            log.debug("Added service authentication token to request: {} {}", 
                template.method(), template.url());
        } else {
            log.warn("Failed to obtain service authentication token for request: {} {}", 
                template.method(), template.url());
        }
    }

    /**
     * Get a valid service token, refreshing if necessary.
     */
    private synchronized String getServiceToken() {
        // Check if we have a cached token that's still valid (with 5 minute buffer)
        if (cachedToken != null && System.currentTimeMillis() < (tokenExpiryTime - 300000)) {
            log.debug("Using cached service token");
            return cachedToken;
        }

        try {
            log.info("Authenticating as service account: {}", serviceUsername);
            
            // Prepare login request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("usernameOrEmail", serviceUsername);
            loginRequest.put("password", servicePassword);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, headers);
            
            // Call auth service login endpoint
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> body = response.getBody();
                cachedToken = (String) body.get("accessToken");
                
                // Calculate token expiry (assuming token is valid for the duration specified in response)
                Object expiresIn = body.get("expiresIn");
                if (expiresIn instanceof Number) {
                    tokenExpiryTime = System.currentTimeMillis() + ((Number) expiresIn).longValue();
                } else {
                    // Default to 1 hour if not specified
                    tokenExpiryTime = System.currentTimeMillis() + 3600000;
                }
                
                log.info("Successfully obtained service token, expires at: {}", 
                    new java.util.Date(tokenExpiryTime));
                return cachedToken;
            } else {
                log.error("Failed to authenticate service account. Status: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Error authenticating service account: {}", e.getMessage(), e);
            // Clear cached token on error
            cachedToken = null;
            tokenExpiryTime = 0;
            return null;
        }
    }
}
