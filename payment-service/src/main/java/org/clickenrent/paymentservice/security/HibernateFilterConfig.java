package org.clickenrent.paymentservice.security;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.clickenrent.contracts.security.TenantContext;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Automatically enables Hibernate tenant filters before repository queries.
 * Filters add WHERE clauses to limit results to current user's companies.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class HibernateFilterConfig {
    
    private final EntityManager entityManager;
    
    @Before("execution(* org.clickenrent.paymentservice.repository.*.*(..))")
    public void enableTenantFilters() {
        // Skip filtering for superadmins
        if (TenantContext.isSuperAdmin()) {
            log.trace("Admin user - Hibernate filters NOT applied");
            return;
        }
        
        // Get user's companies
        List<String> companyIds = TenantContext.getCurrentCompanies();
        if (companyIds.isEmpty()) {
            log.trace("No companies in context - customer or unauthenticated user");
            return;
        }
        
        // Enable filters
        Session session = entityManager.unwrap(Session.class);
        
        // Convert list to SQL IN clause format: 'id1','id2','id3'
        String companyIdsParam = companyIds.stream()
                .map(id -> "'" + id.replace("'", "''") + "'") // SQL injection protection
                .collect(Collectors.joining(","));
        
        // Enable standard company filter
        session.enableFilter("companyFilter")
               .setParameter("companyExternalIds", companyIdsParam);
        
        log.debug("Hibernate tenant filters enabled for companies: {}", companyIds);
    }
}
