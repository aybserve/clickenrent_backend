package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.BikeDTO;
import org.clickenrent.rentalservice.entity.*;
import org.clickenrent.rentalservice.repository.*;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Bike entity and BikeDTO.
 */
@Component
@RequiredArgsConstructor
public class BikeMapper {

    private final BikeStatusRepository bikeStatusRepository;
    private final BatteryChargeStatusRepository batteryChargeStatusRepository;
    private final LockRepository lockRepository;
    private final HubRepository hubRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final BikeTypeRepository bikeTypeRepository;
    private final BikeModelRepository bikeModelRepository;

    public BikeDTO toDto(Bike bike) {
        if (bike == null) {
            return null;
        }

        return BikeDTO.builder()
                .id(bike.getId())
                .externalId(bike.getExternalId())
                .code(bike.getCode())
                .qrCodeUrl(bike.getQrCodeUrl())
                .frameNumber(bike.getFrameNumber())
                .bikeStatusId(bike.getBikeStatus() != null ? bike.getBikeStatus().getId() : null)
                .batteryChargeStatusId(bike.getBatteryChargeStatus() != null ? bike.getBatteryChargeStatus().getId() : null)
                .lockId(bike.getLock() != null ? bike.getLock().getId() : null)
                .vat(bike.getVat())
                .isVatInclude(bike.getIsVatInclude())
                .hubId(bike.getHub() != null ? bike.getHub().getId() : null)
                .coordinatesId(bike.getCoordinates() != null ? bike.getCoordinates().getId() : null)
                .bikeTypeId(bike.getBikeType() != null ? bike.getBikeType().getId() : null)
                .currencyId(bike.getCurrencyId())
                .costPerDay(bike.getCostPerDay())
                .costPerHour(bike.getCostPerHour())
                .costPerWeek(bike.getCostPerWeek())
                .inServiceDate(bike.getInServiceDate())
                .bikeModelId(bike.getBikeModel() != null ? bike.getBikeModel().getId() : null)
                .isB2BRentable(bike.getIsB2BRentable())
                .revenueSharePercent(bike.getRevenueSharePercent())
                .dateCreated(bike.getDateCreated())
                .lastDateModified(bike.getLastDateModified())
                .createdBy(bike.getCreatedBy())
                .lastModifiedBy(bike.getLastModifiedBy())
                .build();
    }

    public Bike toEntity(BikeDTO dto) {
        if (dto == null) {
            return null;
        }

        Bike.BikeBuilder<?, ?> builder = Bike.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .code(dto.getCode())
                .qrCodeUrl(dto.getQrCodeUrl())
                .frameNumber(dto.getFrameNumber())
                .vat(dto.getVat())
                .isVatInclude(dto.getIsVatInclude())
                .currencyId(dto.getCurrencyId())
                .costPerDay(dto.getCostPerDay())
                .costPerHour(dto.getCostPerHour())
                .costPerWeek(dto.getCostPerWeek())
                .inServiceDate(dto.getInServiceDate())
                .isB2BRentable(dto.getIsB2BRentable())
                .revenueSharePercent(dto.getRevenueSharePercent());

        if (dto.getBikeStatusId() != null) {
            builder.bikeStatus(bikeStatusRepository.findById(dto.getBikeStatusId()).orElse(null));
        }
        if (dto.getBatteryChargeStatusId() != null) {
            builder.batteryChargeStatus(batteryChargeStatusRepository.findById(dto.getBatteryChargeStatusId()).orElse(null));
        }
        if (dto.getLockId() != null) {
            builder.lock(lockRepository.findById(dto.getLockId()).orElse(null));
        }
        if (dto.getHubId() != null) {
            builder.hub(hubRepository.findById(dto.getHubId()).orElse(null));
        }
        if (dto.getCoordinatesId() != null) {
            builder.coordinates(coordinatesRepository.findById(dto.getCoordinatesId()).orElse(null));
        }
        if (dto.getBikeTypeId() != null) {
            builder.bikeType(bikeTypeRepository.findById(dto.getBikeTypeId()).orElse(null));
        }
        if (dto.getBikeModelId() != null) {
            builder.bikeModel(bikeModelRepository.findById(dto.getBikeModelId()).orElse(null));
        }

        return builder.build();
    }

    public void updateEntityFromDto(BikeDTO dto, Bike bike) {
        if (dto == null || bike == null) {
            return;
        }

        if (dto.getCode() != null) {
            bike.setCode(dto.getCode());
        }
        if (dto.getQrCodeUrl() != null) {
            bike.setQrCodeUrl(dto.getQrCodeUrl());
        }
        if (dto.getFrameNumber() != null) {
            bike.setFrameNumber(dto.getFrameNumber());
        }
        if (dto.getVat() != null) {
            bike.setVat(dto.getVat());
        }
        if (dto.getIsVatInclude() != null) {
            bike.setIsVatInclude(dto.getIsVatInclude());
        }
        if (dto.getCostPerDay() != null) {
            bike.setCostPerDay(dto.getCostPerDay());
        }
        if (dto.getCostPerHour() != null) {
            bike.setCostPerHour(dto.getCostPerHour());
        }
        if (dto.getCostPerWeek() != null) {
            bike.setCostPerWeek(dto.getCostPerWeek());
        }
        if (dto.getInServiceDate() != null) {
            bike.setInServiceDate(dto.getInServiceDate());
        }
        if (dto.getRevenueSharePercent() != null) {
            bike.setRevenueSharePercent(dto.getRevenueSharePercent());
        }
    }
}
