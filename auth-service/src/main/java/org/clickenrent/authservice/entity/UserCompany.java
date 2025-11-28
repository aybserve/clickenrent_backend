package org.clickenrent.authservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class UserCompany {

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

