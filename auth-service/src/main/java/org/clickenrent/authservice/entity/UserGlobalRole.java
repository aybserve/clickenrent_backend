package org.clickenrent.authservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Join table entity linking users to their global system roles.
 * A user can have multiple global roles (e.g., Admin + B2B).
 */
@Entity
@Table(
    name = "user_global_role",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_global_role",
            columnNames = {"user_id", "global_role_id"}
        )
    }
)
@SQLDelete(sql = "UPDATE user_global_role SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class UserGlobalRole extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Global role is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "global_role_id", nullable = false)
    private GlobalRole globalRole;
}


