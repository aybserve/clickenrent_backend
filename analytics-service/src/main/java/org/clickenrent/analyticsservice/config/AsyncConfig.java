package org.clickenrent.analyticsservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration for asynchronous processing.
 * Enables @Async annotation support for audit logging.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
