package org.clickenrent.rentalservice.config;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Database schema validator that verifies required PostGIS columns exist at startup.
 * This prevents runtime errors when executing spatial queries that depend on the geom column.
 * 
 * CRITICAL: This validator ensures that data.sql executed successfully and created
 * the required geometry column for the /api/v1/bikes/nearby endpoint.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSchemaValidator {
    
    private final EntityManager entityManager;
    
    /**
     * Validates database schema after application startup.
     * Checks if the coordinates.geom column exists which is required for spatial queries.
     * 
     * @throws IllegalStateException if required schema elements are missing
     */
    @EventListener(ApplicationReadyEvent.class)
    public void validateSchema() {
        try {
            log.info("Starting database schema validation...");
            
            // Check if geom column exists in coordinates table
            String sql = """
                SELECT column_name 
                FROM information_schema.columns 
                WHERE table_name = 'coordinates' 
                AND column_name = 'geom'
                """;
            
            List<?> result = entityManager.createNativeQuery(sql).getResultList();
            
            if (result.isEmpty()) {
                String errorMessage = """
                    CRITICAL: coordinates.geom column is missing!
                    PostGIS spatial features will not work.
                    The /api/v1/bikes/nearby endpoint will return 500 errors.
                    
                    SOLUTION: Ensure data.sql executed successfully with Section 0.1 (PostGIS setup).
                    Check application logs for SQL script execution errors.
                    """;
                log.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
            
            // Check if spatial index exists
            String indexSql = """
                SELECT indexname 
                FROM pg_indexes 
                WHERE tablename = 'coordinates' 
                AND indexname = 'idx_coordinates_geom'
                """;
            
            List<?> indexResult = entityManager.createNativeQuery(indexSql).getResultList();
            
            if (indexResult.isEmpty()) {
                log.warn("WARNING: Spatial index idx_coordinates_geom is missing. " +
                        "Nearby bikes queries may be slow. Consider running: " +
                        "CREATE INDEX idx_coordinates_geom ON coordinates USING GIST (geom);");
            } else {
                log.info("✓ Spatial index idx_coordinates_geom exists");
            }
            
            // Check if trigger function exists
            String triggerSql = """
                SELECT tgname 
                FROM pg_trigger 
                WHERE tgname = 'trg_coordinates_geom_update'
                """;
            
            List<?> triggerResult = entityManager.createNativeQuery(triggerSql).getResultList();
            
            if (triggerResult.isEmpty()) {
                log.warn("WARNING: Trigger trg_coordinates_geom_update is missing. " +
                        "Geometry column will not auto-update from lat/lng changes.");
            } else {
                log.info("✓ Trigger trg_coordinates_geom_update exists");
            }
            
            log.info("✓ Database schema validation passed - PostGIS columns present");
            
        } catch (IllegalStateException e) {
            // Re-throw IllegalStateException as-is (critical validation failure)
            throw e;
        } catch (Exception e) {
            log.error("❌ Database schema validation FAILED", e);
            throw new IllegalStateException(
                    "Database schema validation failed. " +
                    "Unable to verify PostGIS configuration: " + e.getMessage(), e);
        }
    }
}
