package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.SupportRequestStatusDTO;
import org.clickenrent.supportservice.entity.SupportRequestStatus;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.SupportRequestStatusMapper;
import org.clickenrent.supportservice.repository.SupportRequestStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing SupportRequestStatus entities.
 */
@Service
@RequiredArgsConstructor
public class SupportRequestStatusService {

    private final SupportRequestStatusRepository supportRequestStatusRepository;
    private final SupportRequestStatusMapper supportRequestStatusMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<SupportRequestStatusDTO> getAll() {
        return supportRequestStatusRepository.findAll().stream()
                .map(supportRequestStatusMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupportRequestStatusDTO getById(Long id) {
        SupportRequestStatus entity = supportRequestStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequestStatus", "id", id));
        return supportRequestStatusMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public SupportRequestStatusDTO getByExternalId(String externalId) {
        SupportRequestStatus entity = supportRequestStatusRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequestStatus", "externalId", externalId));
        return supportRequestStatusMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public SupportRequestStatusDTO getByName(String name) {
        SupportRequestStatus entity = supportRequestStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequestStatus", "name", name));
        return supportRequestStatusMapper.toDto(entity);
    }

    @Transactional
    public SupportRequestStatusDTO create(SupportRequestStatusDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create support request statuses");
        }

        SupportRequestStatus entity = supportRequestStatusMapper.toEntity(dto);
        entity.sanitizeForCreate();
        entity = supportRequestStatusRepository.save(entity);
        return supportRequestStatusMapper.toDto(entity);
    }

    @Transactional
    public SupportRequestStatusDTO update(Long id, SupportRequestStatusDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update support request statuses");
        }

        SupportRequestStatus entity = supportRequestStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequestStatus", "id", id));

        supportRequestStatusMapper.updateEntityFromDto(dto, entity);
        entity = supportRequestStatusRepository.save(entity);
        return supportRequestStatusMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete support request statuses");
        }

        SupportRequestStatus entity = supportRequestStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequestStatus", "id", id));
        supportRequestStatusRepository.delete(entity);
    }
}








