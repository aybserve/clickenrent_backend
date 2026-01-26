package org.clickenrent.searchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for Search Service.
 * 
 * Provides Elasticsearch-based global search across users, bikes, locations, and hubs
 * with multi-tenant isolation and service discovery via Eureka.
 * 
 * @author Vitaliy Shvetsov
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }
}
