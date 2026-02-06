package org.clickenrent.searchservice.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration to load .env file from project root and add to Spring Environment.
 * This runs before any beans are created, ensuring environment variables are available.
 * 
 * @author Vitaliy Shvetsov
 */
@Slf4j
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        try {
            // Try to load .env from project root (parent directory)
            Dotenv dotenv = Dotenv.configure()
                    .directory("../")
                    .ignoreIfMissing()
                    .load();
            
            // Convert dotenv entries to a Map
            Map<String, Object> dotenvMap = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                dotenvMap.put(entry.getKey(), entry.getValue());
                log.debug("Loaded env variable: {} = {}", entry.getKey(), 
                        entry.getKey().contains("PASSWORD") ? "***" : entry.getValue());
            });
            
            // Add to Spring Environment as a property source
            environment.getPropertySources().addFirst(
                    new MapPropertySource("dotenvProperties", dotenvMap)
            );
            
            log.info("✅ Successfully loaded .env file with {} properties", dotenvMap.size());
            
            // Log Elasticsearch configuration (without password)
            log.info("Elasticsearch URI: {}", dotenvMap.get("ES_URIS"));
            log.info("Elasticsearch Username: {}", dotenvMap.get("ES_USERNAME"));
            log.info("Elasticsearch Password: {}", dotenvMap.containsKey("ES_PASSWORD") ? "***SET***" : "NOT SET");
            
        } catch (Exception e) {
            log.warn("⚠️ Could not load .env file: {}", e.getMessage());
            log.warn("   Continuing with system environment variables...");
        }
    }
}
