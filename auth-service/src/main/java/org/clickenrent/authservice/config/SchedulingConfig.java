package org.clickenrent.authservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration for Spring Scheduling.
 * Enables support for @Scheduled annotations in the application.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Spring Boot scheduling configuration
    // @Scheduled methods in components will be automatically discovered and executed
}
