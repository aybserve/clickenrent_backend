package org.clickenrent.notificationservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Entity representing an Expo Push Token for a user's device.
 */
@Entity
@Table(
    name = "push_tokens",
    indexes = {
        @Index(name = "idx_push_tokens_user_external_id", columnList = "user_external_id"),
        @Index(name = "idx_push_tokens_expo_token", columnList = "expo_push_token")
    }
)
@SQLDelete(sql = "UPDATE push_tokens SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class PushToken extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "User external ID is required")
    @Size(max = 100, message = "User external ID must not exceed 100 characters")
    @Column(name = "user_external_id", nullable = false, length = 100)
    private String userExternalId;

    @NotBlank(message = "Expo push token is required")
    @Size(max = 255, message = "Expo push token must not exceed 255 characters")
    @Column(name = "expo_push_token", unique = true, nullable = false, length = 255)
    private String expoPushToken;

    @Size(max = 20, message = "Device type must not exceed 20 characters")
    @Column(name = "device_type", length = 20)
    private String deviceType;

    @Size(max = 100, message = "Device name must not exceed 100 characters")
    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Size(max = 255, message = "Device ID must not exceed 255 characters")
    @Column(name = "device_id", length = 255)
    private String deviceId;

    @Size(max = 50, message = "App version must not exceed 50 characters")
    @Column(name = "app_version", length = 50)
    private String appVersion;

    @Size(max = 100, message = "Device model must not exceed 100 characters")
    @Column(name = "device_model", length = 100)
    private String deviceModel;

    @Size(max = 50, message = "OS version must not exceed 50 characters")
    @Column(name = "os_version", length = 50)
    private String osVersion;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

