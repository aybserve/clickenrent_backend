package org.clickenrent.analyticsservice.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs Flyway migrations AFTER the entire Spring context is initialized,
 * which guarantees Hibernate DDL has already created all tables (staging)
 * or simply validates the schema (production).
 *
 * <p>Spring Boot's default Flyway auto-configuration runs Flyway BEFORE Hibernate,
 * which breaks when migrations reference entity tables. This component uses
 * {@link ApplicationRunner} instead, which only fires after all beans (including
 * {@code entityManagerFactory}) are fully initialized.</p>
 *
 * <p>{@code spring.flyway.enabled=false} is required in application.properties
 * to disable the default auto-configuration.</p>
 *
 * <h3>Testdata behaviour:</h3>
 * <ul>
 *   <li><b>Default (dev/staging):</b> Loads {@code db/testdata/} migrations (sample data)
 *       in addition to {@code db/migration/}.</li>
 *   <li><b>Production:</b> Set {@code FLYWAY_SKIP_TESTDATA=true} to skip sample data
 *       and only load {@code db/migration/}.</li>
 * </ul>
 *
 * <h3>Swap-back to data.sql:</h3>
 * <pre>
 *   FLYWAY_MIGRATE=false   (disables this component)
 *   SQL_INIT_MODE=always   (enables data.sql execution)
 * </pre>
 */
@Component
@Order(1)
@ConditionalOnProperty(name = "flyway.migrate.enabled", havingValue = "true")
public class FlywayConfig implements ApplicationRunner {

    private final DataSource dataSource;
    private final Environment environment;

    public FlywayConfig(DataSource dataSource, Environment environment) {
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> locations = new ArrayList<>(List.of("classpath:db/migration"));

        // Load testdata unless explicitly disabled via FLYWAY_SKIP_TESTDATA=true
        // In production, set FLYWAY_SKIP_TESTDATA=true to skip sample data
        String skipTestdata = environment.getProperty("flyway.skip.testdata", "false");
        if (!"true".equalsIgnoreCase(skipTestdata)) {
            locations.add("classpath:db/testdata");
        }

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(locations.toArray(new String[0]))
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load();
        flyway.migrate();
    }
}
