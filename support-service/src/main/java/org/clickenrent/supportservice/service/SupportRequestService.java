package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.SupportRequestDTO;
import org.clickenrent.supportservice.entity.SupportRequest;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.SupportRequestMapper;
import org.clickenrent.supportservice.repository.SupportRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing SupportRequest entities.
 */
@Service
@RequiredArgsConstructor
public class SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;
    private final SupportRequestMapper supportRequestMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<SupportRequestDTO> getAll() {
        if (securityService.isAdmin()) {
            return supportRequestRepository.findAll().stream()
                    .map(supportRequestMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            Long userId = securityService.getCurrentUserId();
            return supportRequestRepository.findByUserId(userId).stream()
                    .map(supportRequestMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public SupportRequestDTO getById(Long id) {
        SupportRequest entity = supportRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequest", "id", id));
        
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(entity.getUserId())) {
            throw new UnauthorizedException("You don't have permission to access this support request");
        }
        
        return supportRequestMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public SupportRequestDTO getByExternalId(String externalId) {
        SupportRequest entity = supportRequestRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequest", "externalId", externalId));
        
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(entity.getUserId())) {
            throw new UnauthorizedException("You don't have permission to access this support request");
        }
        
        return supportRequestMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<SupportRequestDTO> getByUserId(Long userId) {
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(userId)) {
            throw new UnauthorizedException("You don't have permission to access these support requests");
        }
        
        return supportRequestRepository.findByUserId(userId).stream()
                .map(supportRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupportRequestDTO create(SupportRequestDTO dto) {
        Long currentUserId = securityService.getCurrentUserId();
        if (dto.getUserId() == null) {
            dto.setUserId(currentUserId);
        } else if (!securityService.isAdmin() && !currentUserId.equals(dto.getUserId())) {
            throw new UnauthorizedException("You can only create support requests for yourself");
        }

        SupportRequest entity = supportRequestMapper.toEntity(dto);
        entity = supportRequestRepository.save(entity);
        return supportRequestMapper.toDto(entity);
    }

    @Transactional
    public SupportRequestDTO update(Long id, SupportRequestDTO dto) {
        SupportRequest entity = supportRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequest", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToUser(entity.getUserId())) {
            throw new UnauthorizedException("You don't have permission to update this support request");
        }

        supportRequestMapper.updateEntityFromDto(dto, entity);
        entity = supportRequestRepository.save(entity);
        return supportRequestMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        SupportRequest entity = supportRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequest", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToUser(entity.getUserId())) {
            throw new UnauthorizedException("You don't have permission to delete this support request");
        }

        supportRequestRepository.delete(entity);
    }
}


