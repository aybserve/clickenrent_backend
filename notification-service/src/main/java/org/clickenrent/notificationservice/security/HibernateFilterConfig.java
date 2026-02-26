package org.clickenrent.notificationservice.security;

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
 * 
 * This is a critical security component that ensures:
 * 1. All JPA/Hibernate queries are automatically filtered by company
 * 2. Admins bypass filters (see all data)
 * 3. B2B users only see their companies' data
 * 4. User-scoped notifications (company_external_id IS NULL) are visible to all
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class HibernateFilterConfig {
    
    private final EntityManager entityManager;
    
    @Before("execution(* org.clickenrent.notificationservice.repository.*.*(..))")
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
        
        // Enable company filter (for NotificationLog with hybrid isolation)
        session.enableFilter("companyFilter")
               .setParameter("companyExternalIds", companyIdsParam);
        
        log.debug("Hibernate tenant filters enabled for companies: {}", companyIds);
    }
}
