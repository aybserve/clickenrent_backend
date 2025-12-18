package org.clickenrent.authservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Test configuration to enable JPA Auditing for tests.
 * This is required for @CreatedDate, @CreatedBy, etc. annotations to work.
 */
@TestConfiguration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class TestJpaAuditingConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("test-user");
    }
}


