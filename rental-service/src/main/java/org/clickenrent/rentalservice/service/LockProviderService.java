package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LockProviderDTO;
import org.clickenrent.rentalservice.entity.LockProvider;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.LockProviderMapper;
import org.clickenrent.rentalservice.repository.LockProviderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LockProviderService {

    private final LockProviderRepository lockProviderRepository;
    private final LockProviderMapper lockProviderMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<LockProviderDTO> getAllLockProviders(Pageable pageable) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can view lock providers");
        }

        return lockProviderRepository.findAll(pageable)
                .map(lockProviderMapper::toDto);
    }

    @Transactional(readOnly = true)
    public LockProviderDTO getLockProviderById(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can view lock providers");
        }

        LockProvider lockProvider = lockProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LockProvider", "id", id));
        return lockProviderMapper.toDto(lockProvider);
    }

    @Transactional(readOnly = true)
    public LockProvider getLockProviderEntityById(Long id) {
        return lockProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LockProvider", "id", id));
    }

    @Transactional
    public LockProviderDTO createLockProvider(LockProviderDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create lock providers");
        }

        LockProvider lockProvider = lockProviderMapper.toEntity(dto);
        lockProvider = lockProviderRepository.save(lockProvider);
        return lockProviderMapper.toDto(lockProvider);
    }

    @Transactional
    public LockProviderDTO updateLockProvider(Long id, LockProviderDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update lock providers");
        }

        LockProvider lockProvider = lockProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LockProvider", "id", id));

        lockProviderMapper.updateEntityFromDto(dto, lockProvider);
        lockProvider = lockProviderRepository.save(lockProvider);
        return lockProviderMapper.toDto(lockProvider);
    }

    @Transactional
    public void deleteLockProvider(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete lock providers");
        }

        LockProvider lockProvider = lockProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LockProvider", "id", id));
        lockProviderRepository.delete(lockProvider);
    }
}








