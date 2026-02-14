package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a charging station (extends Product).
 * Uses SINGLE_TABLE inheritance.
 */
@Entity
@DiscriminatorValue("CHARGING_STATION")
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ChargingStation extends Product {

    @NotBlank(message = "Charging station code is required")
    @Size(max = 50, message = "Charging station code must not exceed 50 characters")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Size(max = 500, message = "QR code URL must not exceed 500 characters")
    @Column(name = "qr_code_url", length = 500)
    private String qrCodeUrl;

    @Column(name = "vat", precision = 5, scale = 2)
    private BigDecimal vat;

    @Builder.Default
    @Column(name = "is_vat_include", nullable = true)
    private Boolean isVatInclude = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_station_status_id", nullable = true)
    private ChargingStationStatus chargingStationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinates_id")
    private Coordinates coordinates;

    @Builder.Default
    @Column(name = "is_active", nullable = true)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_id")
    private Hub hub;

    @Column(name = "in_service_date")
    private LocalDate inServiceDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_station_model_id", nullable = true)
    private ChargingStationModel chargingStationModel;
}
