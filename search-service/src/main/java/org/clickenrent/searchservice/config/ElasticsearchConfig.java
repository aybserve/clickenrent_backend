package org.clickenrent.searchservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch configuration for Search Service.
 * 
 * Configures Elasticsearch client connection and repository scanning.
 * Index templates and analyzers are managed by Spring Data Elasticsearch annotations
 * on document classes.
 * 
 * Connection properties are configured in application.properties:
 * - spring.elasticsearch.uris
 * - spring.elasticsearch.username
 * - spring.elasticsearch.password
 * 
 * @author Vitaliy Shvetsov
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "org.clickenrent.searchservice.repository")
public class ElasticsearchConfig {
    
    // Spring Boot auto-configuration handles the Elasticsearch client setup
    // based on application.properties configuration
    
    // Custom beans can be added here if needed for advanced configuration
}
