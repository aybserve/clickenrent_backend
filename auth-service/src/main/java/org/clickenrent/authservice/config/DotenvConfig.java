package org.clickenrent.authservice.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration to load .env file from project root and add to Spring Environment.
 * This runs DURING environment preparation (before profile activation), ensuring
 * SPRING_PROFILES_ACTIVE is available when Spring Boot determines which profiles to activate.
 * 
 * Uses EnvironmentPostProcessor instead of ApplicationContextInitializer because it runs
 * early enough for Spring Boot's profile activation mechanism.
 * 
 * @author Vitaliy Shvetsov
 */
@Slf4j
public class DotenvConfig implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            Dotenv dotenv = null;
            String foundPath = null;
            
            // Get current working directory
            String workingDir = System.getProperty("user.dir");
            log.info("Current working directory: {}", workingDir);
            
            // Try multiple strategies to find .env file
            // Strategy 1: Check common relative paths
            String[] possiblePaths = {"./", "../", "../../"};
            for (String path : possiblePaths) {
                File envFile = new File(path + ".env");
                if (envFile.exists()) {
                    log.info("Found .env file at: {} (absolute: {})", path, envFile.getAbsolutePath());
                    try {
                        dotenv = Dotenv.configure()
                                .directory(path)
                                .load();
                        
                        if (dotenv.entries().iterator().hasNext()) {
                            foundPath = path;
                            break;
                        }
                    } catch (Exception e) {
                        log.debug("Could not load .env from {}: {}", path, e.getMessage());
                    }
                }
            }
            
            // Strategy 2: Walk up the directory tree looking for .env
            if (dotenv == null) {
                File current = new File(workingDir);
                for (int i = 0; i < 5 && current != null; i++) {
                    File envFile = new File(current, ".env");
                    if (envFile.exists()) {
                        log.info("Found .env file by walking up tree at: {}", envFile.getAbsolutePath());
                        try {
                            dotenv = Dotenv.configure()
                                    .directory(current.getAbsolutePath())
                                    .load();
                            
                            if (dotenv.entries().iterator().hasNext()) {
                                foundPath = current.getAbsolutePath();
                                break;
                            }
                        } catch (Exception e) {
                            log.debug("Could not load .env from {}: {}", current, e.getMessage());
                        }
                    }
                    current = current.getParentFile();
                }
            }
            
            if (dotenv == null || !dotenv.entries().iterator().hasNext()) {
                log.warn("⚠️ Could not find .env file - running in production mode");
                log.warn("   Continuing with system/Kubernetes environment variables...");
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
            
            log.info("✅ Successfully loaded .env file from: {}", foundPath);
            log.info("✅ Loaded {} environment variables", dotenvMap.size());
            
        } catch (Exception e) {
            log.warn("⚠️ Could not load .env file: {} - continuing with system environment variables", e.getMessage());
        }
    }
}
