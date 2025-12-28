package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.KeyDTO;
import org.clickenrent.rentalservice.entity.Key;
import org.clickenrent.rentalservice.repository.LockRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Key entity and KeyDTO.
 */
@Component
@RequiredArgsConstructor
public class KeyMapper {

    private final LockRepository lockRepository;

    public KeyDTO toDto(Key key) {
        if (key == null) {
            return null;
        }

        return KeyDTO.builder()
                .id(key.getId())
                .externalId(key.getExternalId())
                .lockId(key.getLock() != null ? key.getLock().getId() : null)
                .build();
    }

    public Key toEntity(KeyDTO dto) {
        if (dto == null) {
            return null;
        }

        Key.KeyBuilder builder = Key.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId());

        if (dto.getLockId() != null) {
            builder.lock(lockRepository.findById(dto.getLockId()).orElse(null));
        }

        return builder.build();
    }
}








