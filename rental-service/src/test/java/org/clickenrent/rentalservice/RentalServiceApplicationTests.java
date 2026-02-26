package org.clickenrent.rentalservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Context load test. Requires PostgreSQL with PostGIS when run with full context.
 * Excluded from default test run via -Dtest=!RentalServiceApplicationTests,!TenantIsolationIntegrationTest
 * when using H2 (e.g. CI without PostGIS).
 */
@SpringBootTest
@org.springframework.test.context.ActiveProfiles("test")
class RentalServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
