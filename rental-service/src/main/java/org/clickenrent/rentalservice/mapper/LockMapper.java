package org.clickenrent.rentalservice.mapper;

import org.clickenrent.rentalservice.dto.LockDTO;
import org.clickenrent.rentalservice.entity.Lock;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Lock entity and LockDTO.
 */
@Component
public class LockMapper {

    public LockDTO toDto(Lock lock) {
        if (lock == null) {
            return null;
        }

        return LockDTO.builder()
                .id(lock.getId())
                .externalId(lock.getExternalId())
                .macAddress(lock.getMacAddress())
                .build();
    }

    public Lock toEntity(LockDTO dto) {
        if (dto == null) {
            return null;
        }

        return Lock.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .macAddress(dto.getMacAddress())
                .build();
    }

    public void updateEntityFromDto(LockDTO dto, Lock lock) {
        if (dto == null || lock == null) {
            return;
        }

        if (dto.getMacAddress() != null) {
            lock.setMacAddress(dto.getMacAddress());
        }
    }
}
