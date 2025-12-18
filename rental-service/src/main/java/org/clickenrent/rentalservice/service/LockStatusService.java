package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LockStatusDTO;
import org.clickenrent.rentalservice.entity.LockStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.LockStatusMapper;
import org.clickenrent.rentalservice.repository.LockStatusRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LockStatusService {

    private final LockStatusRepository lockStatusRepository;
    private final LockStatusMapper lockStatusMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<LockStatusDTO> getAllLockStatuses(Pageable pageable) {
        return lockStatusRepository.findAll(pageable)
                .map(lockStatusMapper::toDto);
    }

    @Transactional(readOnly = true)
    public LockStatusDTO getLockStatusById(Long id) {
        LockStatus lockStatus = lockStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LockStatus", "id", id));
        return lockStatusMapper.toDto(lockStatus);
    }

    @Transactional(readOnly = true)
    public LockStatus getLockStatusByName(String name) {
        return lockStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("LockStatus", "name", name));
    }

    @Transactional
    public LockStatusDTO createLockStatus(LockStatusDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create lock statuses");
        }

        LockStatus lockStatus = lockStatusMapper.toEntity(dto);
        lockStatus = lockStatusRepository.save(lockStatus);
        return lockStatusMapper.toDto(lockStatus);
    }

    @Transactional
    public LockStatusDTO updateLockStatus(Long id, LockStatusDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update lock statuses");
        }

        LockStatus lockStatus = lockStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LockStatus", "id", id));

        lockStatusMapper.updateEntityFromDto(dto, lockStatus);
        lockStatus = lockStatusRepository.save(lockStatus);
        return lockStatusMapper.toDto(lockStatus);
    }

    @Transactional
    public void deleteLockStatus(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete lock statuses");
        }

        LockStatus lockStatus = lockStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LockStatus", "id", id));
        lockStatusRepository.delete(lockStatus);
    }
}

