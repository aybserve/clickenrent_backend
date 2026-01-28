package org.clickenrent.notificationservice.config;

import io.sentry.SentryOptions;
import org.clickenrent.contracts.security.TenantContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Sentry configuration for notification-service.
 * Enriches error events with tenant context and request information.
 */
@Configuration
public class SentryConfig {
    
    /**
     * Configure Sentry to add tenant context to all events.
     * This helps track which companies/tenants are experiencing errors.
     */
    @Bean
    public SentryOptions.BeforeSendCallback beforeSendCallback() {
        return (event, hint) -> {
            try {
                // Add tenant context from TenantContext
                List<String> companies = TenantContext.getCurrentCompanies();
                if (!companies.isEmpty()) {
                    event.setTag("company_ids", String.join(",", companies));
                    event.setTag("tenant_count", String.valueOf(companies.size()));
                }
                
                // Add superadmin flag
                if (TenantContext.isSuperAdmin()) {
                    event.setTag("is_superadmin", "true");
                }
                
                // Add context summary for debugging
                event.setExtra("tenant_context", TenantContext.getContextSummary());
                
            } catch (Exception e) {
                // Don't let Sentry context enrichment fail the request
                event.setExtra("sentry_enrichment_error", e.getMessage());
            }
            
            return event;
        };
    }
}
