package org.clickenrent.rentalservice.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.clickenrent.contracts.security.AuditEvent;
import org.clickenrent.contracts.security.AuditService;
import org.clickenrent.contracts.security.TenantContext;
import org.clickenrent.contracts.security.TenantScoped;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Runtime validation aspect that verifies service layer doesn't leak cross-tenant data.
 * Acts as a safety net to catch bugs in Hibernate filters.
 * Can be disabled in production for performance if filters are proven reliable.
 * 
 * This aspect:
 * 1. Intercepts all service method calls
 * 2. Validates returned entities belong to current user's companies
 * 3. Throws SecurityException if cross-tenant data is detected
 * 4. Logs security violations for audit
 * 
 * Enable/disable via: tenant.validation.enabled=true/false
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "tenant.validation.enabled", havingValue = "true", matchIfMissing = true)
public class TenantValidationAspect {
    
    private final AuditService auditService;
    
    @Around("execution(* org.clickenrent.rentalservice.service.*.*(..))")
    public Object validateTenantIsolation(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        
        // Skip validation for admins
        if (TenantContext.isSuperAdmin()) {
            return result;
        }
        
        List<String> allowedCompanies = TenantContext.getCurrentCompanies();
        if (allowedCompanies.isEmpty()) {
            return result; // Customer user or no context
        }
        
        // Validate results
        validateResult(result, allowedCompanies, joinPoint.getSignature().toShortString());
        
        return result;
    }
    
    private void validateResult(Object result, List<String> allowedCompanies, String methodName) {
        if (result == null) {
            return;
        }
        
        if (result instanceof Collection) {
            validateCollection((Collection<?>) result, allowedCompanies, methodName);
        } else if (result instanceof TenantScoped) {
            validateEntity((TenantScoped) result, allowedCompanies, methodName);
        } else if (result instanceof Page) {
            validateCollection(((Page<?>) result).getContent(), allowedCompanies, methodName);
        }
    }
    
    private void validateCollection(Collection<?> entities, List<String> allowedCompanies, String methodName) {
        for (Object entity : entities) {
            if (entity instanceof TenantScoped) {
                validateEntity((TenantScoped) entity, allowedCompanies, methodName);
            }
        }
    }
    
    private void validateEntity(TenantScoped entity, List<String> allowedCompanies, String methodName) {
        String entityCompany = entity.getCompanyExternalId();
        
        if (entityCompany != null && !allowedCompanies.contains(entityCompany)) {
            log.error("SECURITY VIOLATION: Cross-tenant data leak detected! " +
                    "Method: {}, User companies: {}, Entity company: {}",
                    methodName, allowedCompanies, entityCompany);
            
            // Log audit event
            logSecurityViolation(entity, entityCompany, allowedCompanies, methodName);
            
            // In production, you might want to just log and alert instead of throwing
            throw new SecurityException(
                "Cross-tenant data access blocked: User does not have access to company " + entityCompany
            );
        }
    }
    
    private void logSecurityViolation(TenantScoped entity, String entityCompany, 
                                      List<String> allowedCompanies, String methodName) {
        try {
            HttpServletRequest request = null;
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes) {
                request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            }
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("method", methodName);
            metadata.put("allowedCompanies", allowedCompanies);
            metadata.put("entityCompany", entityCompany);
            metadata.put("entityType", entity.getClass().getSimpleName());
            
            AuditEvent event = AuditEvent.builder()
                .eventType(AuditEvent.EventType.RUNTIME_VALIDATION_FAILURE)
                .userExternalId(TenantContext.getCurrentCompanies().isEmpty() ? null : "B2B_USER")
                .userCompanyIds(String.join(",", allowedCompanies))
                .attemptedCompanyId(entityCompany)
                .resourceType(entity.getClass().getSimpleName())
                .endpoint(request != null ? request.getRequestURI() : methodName)
                .httpMethod(request != null ? request.getMethod() : "UNKNOWN")
                .clientIp(request != null ? request.getRemoteAddr() : null)
                .success(false)
                .errorMessage("Cross-tenant data leak detected")
                .metadata(metadata)
                .build();
            
            auditService.logEvent(event);
        } catch (Exception e) {
            log.error("Failed to log audit event for security violation", e);
        }
    }
}
