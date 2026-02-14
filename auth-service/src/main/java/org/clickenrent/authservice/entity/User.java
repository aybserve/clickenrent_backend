package org.clickenrent.authservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

/**
 * Entity representing a user in the system.
 * Contains user profile information, credentials, and audit fields.
 */
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_user_external_id", columnList = "external_id"),
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_username", columnList = "user_name"),
        @Index(name = "idx_user_provider", columnList = "provider_id, provider_user_id")
    }
)
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(exclude = {"password"}, callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class User extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username must not exceed 100 characters")
    @Column(name = "user_name", nullable = false, unique = true, length = 100)
    private String userName;

    @Size(max = 255, message = "Password must not exceed 255 characters")
    @Column(name = "password", length = 255)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Column(name = "first_name", length = 100)
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Column(name = "last_name", length = 100)
    private String lastName;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Column(name = "phone", length = 20)
    private String phone;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_email_verified")
    private Boolean isEmailVerified = false;

    @Builder.Default
    @Column(name = "is_accepted_terms")
    private Boolean isAcceptedTerms = false;

    @Builder.Default
    @Column(name = "is_accepted_privacy_policy")
    private Boolean isAcceptedPrivacyPolicy = false;

    @Size(max = 50, message = "Provider ID must not exceed 50 characters")
    @Column(name = "provider_id", length = 50)
    private String providerId;

    @Size(max = 255, message = "Provider user ID must not exceed 255 characters")
    @Column(name = "provider_user_id", length = 255)
    private String providerUserId;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
        if (getIsDeleted() == null) {
            setIsDeleted(false);
        }
    }
}


