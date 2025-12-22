package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

/**
 * Entity representing charging station brands.
 */
@Entity
@Table(
    name = "charging_station_brand",
    indexes = {
        @Index(name = "idx_charging_station_brand_external_id", columnList = "external_id"),
        @Index(name = "idx_charging_station_brand_company_id", columnList = "company_id"),
        @Index(name = "idx_charging_station_brand_company_external_id", columnList = "company_external_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class ChargingStationBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotBlank(message = "Charging station brand name is required")
    @Size(max = 100, message = "Charging station brand name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "Company ID is required")
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    // Cross-service reference field using externalId
    @Column(name = "company_external_id", length = 100)
    private String companyExternalId;

    @PrePersist
    public void prePersist() {
        if (externalId == null || externalId.isEmpty()) {
            externalId = UUID.randomUUID().toString();
        }
    }
}




