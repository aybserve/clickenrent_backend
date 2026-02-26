package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.clickenrent.contracts.security.TenantScoped;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

/**
 * Entity representing charging station brands.
 * Implements TenantScoped for multi-tenant isolation.
 */
@Entity
@Table(
    name = "charging_station_brand",
    indexes = {
        @Index(name = "idx_charging_station_brand_external_id", columnList = "external_id"),
        @Index(name = "idx_charging_station_brand_company_external_id", columnList = "company_external_id")
    }
)
@Filter(name = "companyFilter", condition = "company_external_id IN (:companyExternalIds)")
@SQLDelete(sql = "UPDATE charging_station_brand SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class ChargingStationBrand extends BaseAuditEntity implements TenantScoped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotBlank(message = "Charging station brand name is required")
    @Size(max = 100, message = "Charging station brand name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "Company external ID is required")
    @Column(name = "company_external_id", nullable = false, length = 100)
    private String companyExternalId;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }
    
    @Override
    public String getCompanyExternalId() {
        return this.companyExternalId;
    }
}




