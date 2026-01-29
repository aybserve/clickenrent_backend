package org.clickenrent.analyticsservice.repository;

import org.clickenrent.analyticsservice.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for AuditLog entity.
 * Provides database access for security audit logs.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
