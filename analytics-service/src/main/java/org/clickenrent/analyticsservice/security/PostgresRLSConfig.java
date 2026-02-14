package org.clickenrent.analyticsservice.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.clickenrent.contracts.security.TenantContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AOP aspect that sets PostgreSQL session variables for Row Level Security (RLS).
 * This provides database-level tenant isolation as a defense-in-depth measure.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PostgresRLSConfig {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Before("execution(* org.clickenrent.analyticsservice.repository..*(..))")
    public void setRLSContext() {
        boolean isSuperAdmin = TenantContext.isSuperAdmin();
        List<String> companyIds = TenantContext.getCurrentCompanies();
        
        // Set superadmin flag
        jdbcTemplate.execute("SET LOCAL app.is_superadmin = " + isSuperAdmin);
        
        // Set company IDs (comma-separated)
        String companyIdsStr = companyIds.isEmpty() ? "" : String.join(",", companyIds);
        jdbcTemplate.execute("SET LOCAL app.company_external_ids = '" + companyIdsStr + "'");
    }
}
