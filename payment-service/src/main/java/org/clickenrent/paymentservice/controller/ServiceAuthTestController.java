package org.clickenrent.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.client.RentalServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Test controller to verify service-to-service authentication is working.
 * This is a temporary controller for debugging purposes.
 */
@RestController
@RequestMapping("/api/v1/test/service-auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Service Auth Test", description = "Test endpoints for debugging service authentication")
public class ServiceAuthTestController {

    private final RestTemplate loadBalancedRestTemplate;
    private final RentalServiceClient rentalServiceClient;

    @Value("${service.auth.username:service_payment}")
    private String serviceUsername;

    @Value("${service.auth.password:password}")
    private String servicePassword;

    @Value("${service.auth.url:http://auth-service/api/v1/auth/login}")
    private String authUrl;

    /**
     * Test 1: Try to authenticate as service account
     */
    @GetMapping("/authenticate")
    @Operation(summary = "Test service account authentication")
    public ResponseEntity<Map<String, Object>> testAuthentication() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("Testing authentication as service account: {}", serviceUsername);
            log.info("Auth URL: {}", authUrl);
            
            // Prepare login request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("usernameOrEmail", serviceUsername);
            loginRequest.put("password", servicePassword);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, headers);
            
            // Call auth service
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = loadBalancedRestTemplate.postForEntity(authUrl, request, Map.class);
            
            result.put("success", true);
            result.put("statusCode", response.getStatusCode().value());
            result.put("hasBody", response.getBody() != null);
            if (response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> body = response.getBody();
                result.put("hasAccessToken", body.containsKey("accessToken"));
                result.put("hasRefreshToken", body.containsKey("refreshToken"));
            }
            
            log.info("Authentication successful!");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("error", e.getClass().getSimpleName());
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Test 2: Try to call rental-service with Feign client
     */
    @GetMapping("/call-rental-service")
    @Operation(summary = "Test calling rental-service via Feign")
    public ResponseEntity<Map<String, Object>> testRentalServiceCall() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("Testing Feign call to rental-service...");
            
            LocalDate startDate = LocalDate.of(2025, 12, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);
            
            var unpaidRentals = rentalServiceClient.getUnpaidBikeRentals(startDate, endDate);
            
            result.put("success", true);
            result.put("rentalCount", unpaidRentals.size());
            result.put("rentals", unpaidRentals);
            
            log.info("Feign call successful! Found {} rentals", unpaidRentals.size());
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Feign call failed: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("error", e.getClass().getSimpleName());
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * Test 3: Check configuration
     */
    @GetMapping("/config")
    @Operation(summary = "Check service auth configuration")
    public ResponseEntity<Map<String, Object>> checkConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("serviceUsername", serviceUsername);
        result.put("passwordConfigured", servicePassword != null && !servicePassword.isEmpty());
        result.put("authUrl", authUrl);
        result.put("restTemplateClass", loadBalancedRestTemplate.getClass().getName());
        
        return ResponseEntity.ok(result);
    }
}
