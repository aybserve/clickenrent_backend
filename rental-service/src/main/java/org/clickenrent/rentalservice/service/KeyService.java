package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.KeyDTO;
import org.clickenrent.rentalservice.entity.Key;
import org.clickenrent.rentalservice.entity.Lock;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.KeyMapper;
import org.clickenrent.rentalservice.repository.KeyRepository;
import org.clickenrent.rentalservice.repository.LockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyService {

    private final KeyRepository keyRepository;
    private final LockRepository lockRepository;
    private final KeyMapper keyMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<KeyDTO> getKeysByLock(Long lockId) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can view keys");
        }

        Lock lock = lockRepository.findById(lockId)
                .orElseThrow(() -> new ResourceNotFoundException("Lock", "id", lockId));

        return keyRepository.findByLock(lock).stream()
                .map(keyMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public KeyDTO getKeyById(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can view keys");
        }

        Key key = keyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Key", "id", id));
        return keyMapper.toDto(key);
    }

    @Transactional
    public KeyDTO createKey(KeyDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create keys");
        }

        Key key = keyMapper.toEntity(dto);
        key.sanitizeForCreate();
        key = keyRepository.save(key);
        return keyMapper.toDto(key);
    }

    @Transactional
    public void deleteKey(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete keys");
        }

        Key key = keyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Key", "id", id));
        keyRepository.delete(key);
    }

    @Transactional(readOnly = true)
    public KeyDTO findByExternalId(String externalId) {
        Key key = keyRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Key", "externalId", externalId));
        return keyMapper.toDto(key);
    }
}








