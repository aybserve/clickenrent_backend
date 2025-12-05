package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entity representing a rental location.
 * Auto-creates "Default" location when a company is created.
 */
@Entity
@Table(
    name = "location",
    indexes = {
        @Index(name = "idx_location_external_id", columnList = "external_id"),
        @Index(name = "idx_location_company_id", columnList = "company_id"),
        @Index(name = "idx_location_erp_partner_id", columnList = "erp_partner_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
public class Location extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @Size(max = 100, message = "ERP Partner ID must not exceed 100 characters")
    @Column(name = "erp_partner_id", length = 100)
    private String erpPartnerId;

    @NotBlank(message = "Location name is required")
    @Size(max = 255, message = "Location name must not exceed 255 characters")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(name = "address", length = 500)
    private String address;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @NotNull(message = "Company ID is required")
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Builder.Default
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    @Size(max = 1000, message = "Directions must not exceed 1000 characters")
    @Column(name = "directions", length = 1000)
    private String directions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinates_id")
    private Coordinates coordinates;
}
