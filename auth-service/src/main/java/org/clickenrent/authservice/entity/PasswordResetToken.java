package org.clickenrent.authservice.entity;

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
 * Entity representing password reset tokens.
 * Stores reset codes with expiration and attempt tracking for security.
 */
@Entity
@Table(
    name = "password_reset_token",
    indexes = {
        @Index(name = "idx_password_reset_user", columnList = "user_id"),
        @Index(name = "idx_password_reset_email", columnList = "email"),
        @Index(name = "idx_password_reset_token", columnList = "token")
    }
)
@SQLDelete(sql = "UPDATE password_reset_token SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class PasswordResetToken extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100, message = "External ID must not exceed 100 characters")
    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @NotBlank(message = "Token is required")
    @Size(max = 6, message = "Token must not exceed 6 characters")
    @Column(name = "token", nullable = false, length = 6)
    private String token;

    @NotNull(message = "Expiration time is required")
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Builder.Default
    @Column(name = "attempts", nullable = false)
    private Integer attempts = 0;

    @Builder.Default
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "used_ip_address", length = 45)
    private String usedIpAddress;

    @Column(name = "used_user_agent", length = 500)
    private String usedUserAgent;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getExternalId() {
        return externalId;
    }

    @Override
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
