package org.clickenrent.analyticsservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.security.AuditEvent;
import org.clickenrent.contracts.security.AuditService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Simple implementation of AuditService for logging security events.
 * Currently logs to application logs only.
 * TODO: Add database persistence with AuditLog entity and repository if needed.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {
    
    private final ObjectMapper objectMapper;
    
    @Override
    @Async
    public void logEvent(AuditEvent event) {
        try {
            String metadataJson = "";
            if (event.getMetadata() != null && !event.getMetadata().isEmpty()) {
                try {
                    metadataJson = objectMapper.writeValueAsString(event.getMetadata());
                } catch (JsonProcessingException e) {
                    log.error("Failed to serialize metadata", e);
                    metadataJson = event.getMetadata().toString();
                }
            }
            
            // Log to application logs
            log.warn("SECURITY_EVENT: type={}, user={}, companies={}, resource={}, endpoint={}, success={}, metadata={}", 
                event.getEventType(), 
                event.getUserExternalId(),
                event.getUserCompanyIds(),
                event.getResourceType(),
                event.getEndpoint(),
                event.isSuccess(),
                metadataJson);
            
            if (!event.isSuccess() && event.getErrorMessage() != null) {
                log.error("Security event failed: {}", event.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("Failed to log audit event: {}", event, e);
        }
    }
}
