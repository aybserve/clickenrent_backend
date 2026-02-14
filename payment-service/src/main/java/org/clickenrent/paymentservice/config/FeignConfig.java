package org.clickenrent.paymentservice.config;

import feign.RequestInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Feign client configuration for payment service.
 * Registers the service authentication interceptor for all Feign clients.
 */
@Configuration
public class FeignConfig {

    /**
     * Load-balanced RestTemplate for service-to-service calls with Eureka service discovery.
     * Used by ServiceAuthenticationInterceptor to authenticate with auth-service.
     */
    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RequestInterceptor requestInterceptor(RestTemplate loadBalancedRestTemplate) {
        return new ServiceAuthenticationInterceptor(loadBalancedRestTemplate);
    }
}
