package org.clickenrent.notificationservice.repository;

import org.clickenrent.notificationservice.entity.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for NotificationLog entity.
 */
@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    /**
     * Find all notification logs for a specific user
     */
    Page<NotificationLog> findByUserExternalId(String userExternalId, Pageable pageable);

    /**
     * Find notification logs by type
     */
    List<NotificationLog> findByNotificationType(String notificationType);

    /**
     * Find notification logs by status
     */
    List<NotificationLog> findByStatus(String status);

    /**
     * Find failed notifications for retry
     */
    List<NotificationLog> findByStatusAndCreatedAtAfter(String status, LocalDateTime after);

    /**
     * Count notifications by user and status
     */
    long countByUserExternalIdAndStatus(String userExternalId, String status);
}

