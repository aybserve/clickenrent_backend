package org.clickenrent.notificationservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity representing a log of a notification that was sent (or attempted to be sent).
 */
@Entity
@Table(
    name = "notification_logs",
    indexes = {
        @Index(name = "idx_notification_logs_user_external_id", columnList = "user_external_id"),
        @Index(name = "idx_notification_logs_type", columnList = "notification_type"),
        @Index(name = "idx_notification_logs_status", columnList = "status")
    }
)
@SQLDelete(sql = "UPDATE notification_logs SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class NotificationLog extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "User external ID is required")
    @Size(max = 100, message = "User external ID must not exceed 100 characters")
    @Column(name = "user_external_id", nullable = false, length = 100)
    private String userExternalId;

    @Size(max = 50, message = "Notification type must not exceed 50 characters")
    @Column(name = "notification_type", length = 50)
    private String notificationType;

    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "jsonb")
    private Map<String, Object> data;

    @NotBlank(message = "Status is required")
    @Size(max = 20, message = "Status must not exceed 20 characters")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Size(max = 255, message = "Expo receipt ID must not exceed 255 characters")
    @Column(name = "expo_receipt_id", length = 255)
    private String expoReceiptId;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

