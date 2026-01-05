package org.clickenrent.paymentservice.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.clickenrent.contracts.security.TenantContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sets PostgreSQL session variables before each database query.
 * These variables are used by Row Level Security policies to enforce tenant isolation.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PostgresRLSConfig {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Before("execution(* org.clickenrent.paymentservice.repository.*.*(..))")
    public void setPostgresSessionVariables() {
        boolean isAdmin = TenantContext.isSuperAdmin();
        List<String> companyIds = TenantContext.getCurrentCompanies();
        
        try {
            // Set superadmin flag
            jdbcTemplate.execute(String.format(
                "SET LOCAL app.is_superadmin = %s", isAdmin
            ));
            
            // Set company IDs (comma-separated)
            if (!companyIds.isEmpty()) {
                String companyIdsParam = String.join(",", companyIds);
                jdbcTemplate.execute(String.format(
                    "SET LOCAL app.company_external_ids = '%s'", 
                    companyIdsParam.replace("'", "''") // SQL injection protection
                ));
                log.trace("PostgreSQL RLS context set: admin={}, companies={}", isAdmin, companyIdsParam);
            } else {
                jdbcTemplate.execute("SET LOCAL app.company_external_ids = ''");
                log.trace("PostgreSQL RLS context set: admin={}, companies=<none>", isAdmin);
            }
        } catch (Exception e) {
            log.error("Failed to set PostgreSQL session variables for RLS", e);
            // Don't throw - Hibernate filters still provide protection
        }
    }
}
