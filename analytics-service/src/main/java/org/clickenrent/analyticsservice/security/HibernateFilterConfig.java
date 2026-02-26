package org.clickenrent.analyticsservice.security;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.clickenrent.contracts.security.TenantContext;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AOP aspect that automatically enables Hibernate filters before repository calls.
 * This ensures tenant isolation at the ORM level.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class HibernateFilterConfig {
    
    private final EntityManager entityManager;
    
    @Before("execution(* org.clickenrent.analyticsservice.repository..*(..))")
    public void enableFilters() {
        if (TenantContext.isSuperAdmin()) {
            return; // Superadmins bypass filters
        }
        
        List<String> companyIds = TenantContext.getCurrentCompanies();
        if (companyIds.isEmpty()) {
            return; // Customer user or no context
        }
        
        Session session = entityManager.unwrap(Session.class);
        
        // Enable company filter
        if (session.getEnabledFilter("companyFilter") == null) {
            session.enableFilter("companyFilter")
                .setParameterList("companyExternalIds", companyIds);
        }
    }
}
