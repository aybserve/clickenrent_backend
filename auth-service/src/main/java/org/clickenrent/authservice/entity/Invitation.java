package org.clickenrent.authservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Entity representing a user invitation to join a company.
 * Used for inviting B2B users to register and join a specific company.
 */
@Entity
@Table(
    name = "invitation",
    indexes = {
        @Index(name = "idx_invitation_token", columnList = "token"),
        @Index(name = "idx_invitation_email", columnList = "email"),
        @Index(name = "idx_invitation_status", columnList = "status")
    }
)
@SQLDelete(sql = "UPDATE invitation SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(exclude = {"invitedBy", "company"}, callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Invitation extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100, message = "External ID must not exceed 100 characters")
    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @NotBlank(message = "Token is required")
    @Size(max = 255, message = "Token must not exceed 255 characters")
    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    @NotNull(message = "Inviter is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_user_id", nullable = false)
    private User invitedBy;

    @NotNull(message = "Company is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvitationStatus status;

    @NotNull(message = "Expiration date is required")
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

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

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = InvitationStatus.PENDING;
        }
    }

    /**
     * Check if the invitation has expired.
     * @return true if current time is after expiration time
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Check if the invitation is valid (PENDING and not expired).
     * @return true if invitation can be used
     */
    public boolean isValid() {
        return status == InvitationStatus.PENDING && !isExpired();
    }
}

