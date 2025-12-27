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
 * Entity representing email verification codes.
 * Stores verification codes with expiration and attempt tracking.
 */
@Entity
@Table(
    name = "email_verification",
    indexes = {
        @Index(name = "idx_email_verification_user", columnList = "user_id"),
        @Index(name = "idx_email_verification_code", columnList = "code"),
        @Index(name = "idx_email_verification_email", columnList = "email")
    }
)
@SQLDelete(sql = "UPDATE email_verification SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class EmailVerification extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @NotBlank(message = "Code is required")
    @Size(max = 6, message = "Code must not exceed 6 characters")
    @Column(name = "code", nullable = false, length = 6)
    private String code;

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
}







