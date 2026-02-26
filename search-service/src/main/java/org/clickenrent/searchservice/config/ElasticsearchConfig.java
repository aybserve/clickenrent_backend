package org.clickenrent.searchservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.searchservice.document.BikeDocument;
import org.clickenrent.searchservice.document.HubDocument;
import org.clickenrent.searchservice.document.LocationDocument;
import org.clickenrent.searchservice.document.UserDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
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
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableElasticsearchRepositories(basePackages = "org.clickenrent.searchservice.repository")
public class ElasticsearchConfig {
    
    private final ElasticsearchOperations elasticsearchOperations;
    
    /**
     * Log Elasticsearch configuration at startup for debugging.
     */
    @Bean
    public CommandLineRunner logElasticsearchConfig(
            @Value("${spring.elasticsearch.uris}") String esUris,
            @Value("${spring.elasticsearch.username}") String esUsername,
            @Value("${spring.elasticsearch.password:NOT_SET}") String esPassword) {
        return args -> {
            log.info("=".repeat(60));
            log.info("ELASTICSEARCH CONFIGURATION:");
            log.info("  URI: {}", esUris);
            log.info("  Username: {}", esUsername);
            log.info("  Password: {}", esPassword.equals("NOT_SET") ? "NOT SET!!!" : "***SET*** (length: " + esPassword.length() + ")");
            log.info("=".repeat(60));
        };
    }
    
    /**
     * Create Elasticsearch indices at application startup if they don't exist.
     * This ensures all required indices are available before any indexing operations.
     * Uses CommandLineRunner to run after application context is fully initialized.
     */
    @Bean
    public CommandLineRunner initializeElasticsearchIndices() {
        return args -> {
            log.info("Starting Elasticsearch indices initialization...");
            
            try {
                createIndexIfNotExists(BikeDocument.class, "bikes");
                createIndexIfNotExists(LocationDocument.class, "locations");
                createIndexIfNotExists(UserDocument.class, "users");
                createIndexIfNotExists(HubDocument.class, "hubs");
                log.info("✅ Elasticsearch indices verification completed successfully");
            } catch (Exception e) {
                log.error("❌ CRITICAL: Failed to create Elasticsearch indices. Application may not work correctly!", e);
                // Don't throw - let app start but log the error prominently
            }
        };
    }
    
    /**
     * Create index for given document class if it doesn't exist.
     * Uses createOrUpdateIndex() to handle both creation and updates safely.
     */
    private void createIndexIfNotExists(Class<?> documentClass, String indexName) {
        try {
            IndexOperations indexOps = elasticsearchOperations.indexOps(documentClass);
            
            log.info("Ensuring Elasticsearch index '{}' exists...", indexName);
            
            // Try to create - if exists, this will just ensure mapping is up to date
            // Don't trust exists() - it can return cached results
            try {
                boolean created = indexOps.create();
                if (created) {
                    log.info("Created new index '{}'", indexName);
                }
            } catch (Exception e) {
                // Index might already exist - that's fine
                if (e.getMessage() != null && e.getMessage().contains("resource_already_exists")) {
                    log.info("Index '{}' already exists", indexName);
                } else {
                    throw e;
                }
            }
            
            // Always ensure mapping is correct
            indexOps.putMapping();
            indexOps.refresh();
            log.info("✅ Index '{}' ready with mapping", indexName);
            
        } catch (Exception e) {
            log.error("❌ Error with index '{}': {}", indexName, e.getMessage(), e);
            // Don't throw - let app continue but log prominently
        }
    }
}
