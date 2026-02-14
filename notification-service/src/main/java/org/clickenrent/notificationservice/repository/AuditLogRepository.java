package org.clickenrent.notificationservice.repository;

import org.clickenrent.notificationservice.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for audit log operations.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    /**
     * Find audit logs by user external ID.
     */
    List<AuditLog> findByUserExternalIdOrderByTimestampDesc(String userExternalId);
    
    /**
     * Find audit logs by event type.
     */
    List<AuditLog> findByEventTypeOrderByTimestampDesc(String eventType);
    
    /**
     * Find audit logs within a time range.
     */
    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find failed audit logs (success = false).
     */
    List<AuditLog> findBySuccessFalseOrderByTimestampDesc();
}
