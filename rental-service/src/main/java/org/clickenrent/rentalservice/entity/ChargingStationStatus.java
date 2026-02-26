package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing charging station status.
 * Examples: Idle, Pre charging, Charging, Fully charged, Error
 */
@Entity
@Table(name = "charging_station_status")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(of = "id", callSuper = false)
public class ChargingStationStatus extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, length = 100)
    private String externalId;

    @NotBlank(message = "Charging station status name is required")
    @Size(max = 50, message = "Charging station status name must not exceed 50 characters")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
}








