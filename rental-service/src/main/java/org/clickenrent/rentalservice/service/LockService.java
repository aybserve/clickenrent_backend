package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LockDTO;
import org.clickenrent.rentalservice.entity.Lock;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.LockMapper;
import org.clickenrent.rentalservice.repository.LockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LockService {

    private final LockRepository lockRepository;
    private final LockMapper lockMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<LockDTO> getAllLocks(Pageable pageable) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can view locks");
        }

        return lockRepository.findAll(pageable)
                .map(lockMapper::toDto);
    }

    @Transactional(readOnly = true)
    public LockDTO getLockById(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can view locks");
        }

        Lock lock = lockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lock", "id", id));
        return lockMapper.toDto(lock);
    }

    @Transactional
    public LockDTO createLock(LockDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create locks");
        }

        Lock lock = lockMapper.toEntity(dto);
        lock = lockRepository.save(lock);
        return lockMapper.toDto(lock);
    }

    @Transactional
    public LockDTO updateLock(Long id, LockDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update locks");
        }

        Lock lock = lockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lock", "id", id));

        lockMapper.updateEntityFromDto(dto, lock);
        lock = lockRepository.save(lock);
        return lockMapper.toDto(lock);
    }

    @Transactional
    public void deleteLock(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete locks");
        }

        Lock lock = lockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lock", "id", id));
        lockRepository.delete(lock);
    }
}
