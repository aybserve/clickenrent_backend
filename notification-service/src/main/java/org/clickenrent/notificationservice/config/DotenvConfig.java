package org.clickenrent.notificationservice.config;

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
            Dotenv dotenv = null;
            
            // Try multiple locations for .env file
            String[] possiblePaths = {"./", "../", "../../"};
            for (String path : possiblePaths) {
                try {
                    dotenv = Dotenv.configure()
                            .directory(path)
                            .ignoreIfMissing()
                            .load();
                    
                    // If we found any entries, we've loaded the file successfully
                    if (dotenv.entries().iterator().hasNext()) {
                        log.info("✅ Found .env file at: {}", path);
                        break;
                    }
                } catch (Exception e) {
                    // Continue to next path
                }
            }
            
            if (dotenv == null || !dotenv.entries().iterator().hasNext()) {
                log.warn("⚠️ Could not find .env file in any location");
                log.warn("   Continuing with system environment variables...");
                return;
            }
            
            // Convert dotenv entries to a Map
            Map<String, Object> dotenvMap = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                dotenvMap.put(entry.getKey(), entry.getValue());
                log.debug("Loaded env variable: {} = {}", entry.getKey(), 
                        entry.getKey().contains("PASSWORD") || entry.getKey().contains("SECRET") || entry.getKey().contains("KEY") ? "***" : entry.getValue());
            });
            
            // Add to Spring Environment as a property source with highest priority
            environment.getPropertySources().addFirst(
                    new MapPropertySource("dotenvProperties", dotenvMap)
            );
            
            log.info("✅ Successfully loaded .env file with {} properties", dotenvMap.size());
            
        } catch (Exception e) {
            log.error("⚠️ Error loading .env file: {}", e.getMessage(), e);
            log.warn("   Continuing with system environment variables...");
        }
    }
}
