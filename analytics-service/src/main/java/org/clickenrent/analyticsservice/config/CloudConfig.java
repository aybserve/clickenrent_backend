package org.clickenrent.analyticsservice.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Enables Feign clients and Eureka discovery in non-test environments.
 * Excluded when profile "test" is active so that @WebMvcTest can load without Feign/Eureka.
 */
@Configuration
@Profile("!test")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "org.clickenrent.analyticsservice.client")
public class CloudConfig {
}
