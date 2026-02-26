package org.clickenrent.gateway.config;

import io.sentry.SentryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sentry configuration for gateway.
 * Enriches error events with request information.
 */
@Configuration
public class SentryConfig {
    
    /**
     * Configure Sentry to add gateway-specific context to all events.
     * Gateway doesn't have TenantContext as it's the entry point.
     */
    @Bean
    public SentryOptions.BeforeSendCallback beforeSendCallback() {
        return (event, hint) -> {
            try {
                // Add gateway-specific tags
                event.setTag("gateway", "true");
                event.setTag("entry_point", "api_gateway");
                
            } catch (Exception e) {
                // Don't let Sentry context enrichment fail the request
                event.setExtra("sentry_enrichment_error", e.getMessage());
            }
            
            return event;
        };
    }
}
