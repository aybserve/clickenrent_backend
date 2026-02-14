package org.clickenrent.rentalservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.security.AuditEvent;
import org.clickenrent.contracts.security.AuditService;
import org.clickenrent.rentalservice.entity.AuditLog;
import org.clickenrent.rentalservice.repository.AuditLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementation of AuditService for logging security events.
 * Uses async processing to avoid blocking request threads.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {
    
    private final AuditLogRepository repository;
    private final ObjectMapper objectMapper;
    
    @Override
    @Async
    public void logEvent(AuditEvent event) {
        try {
            AuditLog auditLog = toEntity(event);
            repository.save(auditLog);
            
            log.warn("SECURITY_EVENT: type={}, user={}, resource={}, success={}", 
                event.getEventType(), 
                event.getUserExternalId(), 
                event.getResourceType(),
                event.isSuccess());
        } catch (Exception e) {
            log.error("Failed to save audit event: {}", event, e);
        }
    }
    
    /**
     * Convert AuditEvent DTO to AuditLog entity.
     */
    private AuditLog toEntity(AuditEvent event) {
        String metadataJson = null;
        if (event.getMetadata() != null && !event.getMetadata().isEmpty()) {
            try {
                metadataJson = objectMapper.writeValueAsString(event.getMetadata());
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize metadata", e);
            }
        }
        
        return AuditLog.builder()
            .eventType(event.getEventType() != null ? event.getEventType().name() : "UNKNOWN")
            .userExternalId(event.getUserExternalId())
            .companyExternalIds(event.getUserCompanyIds())
            .resourceType(event.getResourceType())
            .resourceId(event.getResourceId())
            .endpoint(event.getEndpoint())
            .httpMethod(event.getHttpMethod())
            .clientIp(event.getClientIp())
            .success(event.isSuccess())
            .errorMessage(event.getErrorMessage())
            .metadata(metadataJson)
            .timestamp(event.getTimestamp())
            .build();
    }
}
