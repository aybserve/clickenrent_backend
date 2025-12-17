package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LockDTO;
import org.clickenrent.rentalservice.entity.Lock;
import org.clickenrent.rentalservice.service.LockProviderService;
import org.clickenrent.rentalservice.service.LockStatusService;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Lock entity and LockDTO.
 */
@Component
@RequiredArgsConstructor
public class LockMapper {

    private final LockStatusMapper lockStatusMapper;
    private final LockProviderMapper lockProviderMapper;
    private final LockStatusService lockStatusService;
    private final LockProviderService lockProviderService;

    public LockDTO toDto(Lock lock) {
        if (lock == null) {
            return null;
        }

        return LockDTO.builder()
                .id(lock.getId())
                .externalId(lock.getExternalId())
                .macAddress(lock.getMacAddress())
                .lockStatus(lock.getLockStatus() != null ? lockStatusMapper.toDto(lock.getLockStatus()) : null)
                .lockProvider(lock.getLockProvider() != null ? lockProviderMapper.toDto(lock.getLockProvider()) : null)
                .batteryLevel(lock.getBatteryLevel())
                .lastSeenAt(lock.getLastSeenAt())
                .firmwareVersion(lock.getFirmwareVersion())
                .build();
    }

    public Lock toEntity(LockDTO dto) {
        if (dto == null) {
            return null;
        }

        Lock.LockBuilder builder = Lock.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .macAddress(dto.getMacAddress())
                .batteryLevel(dto.getBatteryLevel())
                .lastSeenAt(dto.getLastSeenAt())
                .firmwareVersion(dto.getFirmwareVersion());

        if (dto.getLockStatus() != null && dto.getLockStatus().getId() != null) {
            builder.lockStatus(lockStatusService.getLockStatusByName(dto.getLockStatus().getName()));
        }

        if (dto.getLockProvider() != null && dto.getLockProvider().getId() != null) {
            builder.lockProvider(lockProviderService.getLockProviderEntityById(dto.getLockProvider().getId()));
        }

        return builder.build();
    }

    public void updateEntityFromDto(LockDTO dto, Lock lock) {
        if (dto == null || lock == null) {
            return;
        }

        if (dto.getMacAddress() != null) {
            lock.setMacAddress(dto.getMacAddress());
        }
        if (dto.getBatteryLevel() != null) {
            lock.setBatteryLevel(dto.getBatteryLevel());
        }
        if (dto.getLastSeenAt() != null) {
            lock.setLastSeenAt(dto.getLastSeenAt());
        }
        if (dto.getFirmwareVersion() != null) {
            lock.setFirmwareVersion(dto.getFirmwareVersion());
        }
        if (dto.getLockStatus() != null && dto.getLockStatus().getId() != null) {
            lock.setLockStatus(lockStatusService.getLockStatusByName(dto.getLockStatus().getName()));
        }
        if (dto.getLockProvider() != null && dto.getLockProvider().getId() != null) {
            lock.setLockProvider(lockProviderService.getLockProviderEntityById(dto.getLockProvider().getId()));
        }
    }
}
