package org.clickenrent.notificationservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Entity representing user preferences for notification types.
 */
@Entity
@Table(
    name = "notification_preferences",
    indexes = {
        @Index(name = "idx_notification_preferences_user_external_id", columnList = "user_external_id")
    }
)
@SQLDelete(sql = "UPDATE notification_preferences SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class NotificationPreference extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "User external ID is required")
    @Size(max = 100, message = "User external ID must not exceed 100 characters")
    @Column(name = "user_external_id", unique = true, nullable = false, length = 100)
    private String userExternalId;

    @Builder.Default
    @Column(name = "rental_updates_enabled", nullable = false)
    private Boolean rentalUpdatesEnabled = true;

    @Builder.Default
    @Column(name = "payment_updates_enabled", nullable = false)
    private Boolean paymentUpdatesEnabled = true;

    @Builder.Default
    @Column(name = "support_messages_enabled", nullable = false)
    private Boolean supportMessagesEnabled = true;

    @Builder.Default
    @Column(name = "marketing_enabled", nullable = false)
    private Boolean marketingEnabled = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

