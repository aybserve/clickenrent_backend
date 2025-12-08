package org.clickenrent.rentalservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a bike (extends Product).
 * Uses SINGLE_TABLE inheritance.
 */
@Entity
@DiscriminatorValue("BIKE")
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Bike extends Product {

    @NotBlank(message = "Bike code is required")
    @Size(max = 50, message = "Bike code must not exceed 50 characters")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Size(max = 500, message = "QR code URL must not exceed 500 characters")
    @Column(name = "qr_code_url", length = 500)
    private String qrCodeUrl;

    @Size(max = 100, message = "Frame number must not exceed 100 characters")
    @Column(name = "frame_number", length = 100)
    private String frameNumber;

//    @NotNull(message = "Bike status is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_status_id", nullable = true)
    private BikeStatus bikeStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battery_charge_status_id")
    private BatteryChargeStatus batteryChargeStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lock_id")
    private Lock lock;

    @Column(name = "vat", precision = 5, scale = 2)
    private BigDecimal vat;

    @Builder.Default
    @Column(name = "is_vat_include", nullable = true)
    private Boolean isVatInclude = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_id")
    private Hub hub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinates_id")
    private Coordinates coordinates;

//    @NotNull(message = "Bike type is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_type_id", nullable = true)
    private BikeType bikeType;

    @Column(name = "currency_id")
    private Long currencyId;

    @Column(name = "in_service_date")
    private LocalDate inServiceDate;

//    @NotNull(message = "Bike model is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bike_model_id", nullable = true)
    private BikeModel bikeModel;

    @Column(name = "revenue_share_percent", precision = 5, scale = 2)
    private BigDecimal revenueSharePercent;
}
