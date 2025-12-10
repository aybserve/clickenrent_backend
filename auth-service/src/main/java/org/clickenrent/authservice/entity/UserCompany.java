package org.clickenrent.authservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Join table entity linking users to companies with their respective company roles.
 * A user can be associated with multiple companies, each with a specific role.
 */
@Entity
@Table(
    name = "user_company",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_company",
            columnNames = {"user_id", "company_id"}
        )
    }
)
@SQLDelete(sql = "UPDATE user_company SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class UserCompany extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Company is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotNull(message = "Company role is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_role_id", nullable = false)
    private CompanyRole companyRole;
}


