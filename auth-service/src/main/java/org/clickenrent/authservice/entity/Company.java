package org.clickenrent.authservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity representing a company/organization in the system.
 * Companies can be hotels, B&Bs, or other types of rental properties.
 */
@Entity
@Table(
    name = "company",
    indexes = {
        @Index(name = "idx_company_external_id", columnList = "external_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @Size(max = 255, message = "Website must not exceed 255 characters")
    @Column(name = "website", length = 255)
    private String website;

    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    @Column(name = "logo", length = 500)
    private String logo;

    @Size(max = 100, message = "ERP Partner ID must not exceed 100 characters")
    @Column(name = "erp_partner_id", length = 100)
    private String erpPartnerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_type_id")
    private CompanyType companyType;
}


