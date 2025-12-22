package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.rental.BikeDTO;
import org.clickenrent.supportservice.client.BikeServiceClient;
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
@Slf4j
public class SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;
    private final SupportRequestMapper supportRequestMapper;
    private final SecurityService securityService;
    private final BikeServiceClient bikeServiceClient;

    @Transactional(readOnly = true)
    public List<SupportRequestDTO> getAll() {
        if (securityService.isAdmin()) {
            return supportRequestRepository.findAll().stream()
                    .map(supportRequestMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            String userExternalId = securityService.getCurrentUserExternalId();
            if (userExternalId == null) {
                log.error("Failed to get current user external ID from JWT");
                return List.of();
            }
            return supportRequestRepository.findByUserExternalId(userExternalId).stream()
                    .map(supportRequestMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public SupportRequestDTO getById(Long id) {
        SupportRequest entity = supportRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequest", "id", id));
        
        if (!securityService.isAdmin()) {
            String currentUserExternalId = securityService.getCurrentUserExternalId();
            if (currentUserExternalId == null || !currentUserExternalId.equals(entity.getUserExternalId())) {
                throw new UnauthorizedException("You don't have permission to access this support request");
            }
        }
        
        return supportRequestMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public SupportRequestDTO getByExternalId(String externalId) {
        SupportRequest entity = supportRequestRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequest", "externalId", externalId));
        
        if (!securityService.isAdmin()) {
            String currentUserExternalId = securityService.getCurrentUserExternalId();
            if (currentUserExternalId == null || !currentUserExternalId.equals(entity.getUserExternalId())) {
                throw new UnauthorizedException("You don't have permission to access this support request");
            }
        }
        
        return supportRequestMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<SupportRequestDTO> getByUserExternalId(String userExternalId) {
        if (!securityService.isAdmin()) {
            String currentUserExternalId = securityService.getCurrentUserExternalId();
            if (currentUserExternalId == null || !currentUserExternalId.equals(userExternalId)) {
                throw new UnauthorizedException("You don't have permission to access these support requests");
            }
        }
        
        return supportRequestRepository.findByUserExternalId(userExternalId).stream()
                .map(supportRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupportRequestDTO> getByBikeExternalId(String bikeExternalId) {
        // Authorization: admins can view all support requests for any bike
        // Regular users can only view support requests they created (filtered by repository query)
        if (!securityService.isAdmin()) {
            // Verify the bike exists by calling bike service
            try {
                bikeServiceClient.getBikeByExternalId(bikeExternalId);
            } catch (Exception e) {
                log.error("Failed to verify bike exists for bikeExternalId: {}", bikeExternalId, e);
                throw new ResourceNotFoundException("Bike", "externalId", bikeExternalId);
            }
            
            // For non-admin users, filter to only show their own support requests
            String currentUserExternalId = securityService.getCurrentUserExternalId();
            if (currentUserExternalId == null) {
                throw new UnauthorizedException("Failed to get current user external ID from JWT");
            }
            
            return supportRequestRepository.findByBikeExternalId(bikeExternalId).stream()
                    .filter(sr -> currentUserExternalId.equals(sr.getUserExternalId()))
                    .map(supportRequestMapper::toDto)
                    .collect(Collectors.toList());
        }
        
        return supportRequestRepository.findByBikeExternalId(bikeExternalId).stream()
                .map(supportRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupportRequestDTO create(SupportRequestDTO dto) {
        String currentUserExternalId = securityService.getCurrentUserExternalId();
        if (currentUserExternalId == null) {
            throw new RuntimeException("Failed to get current user external ID from JWT");
        }
        
        // Set userExternalId if not provided or verify if provided
        if (dto.getUserExternalId() == null) {
            dto.setUserExternalId(currentUserExternalId);
        } else if (!securityService.isAdmin() && !currentUserExternalId.equals(dto.getUserExternalId())) {
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

        if (!securityService.isAdmin()) {
            String currentUserExternalId = securityService.getCurrentUserExternalId();
            if (currentUserExternalId == null || !currentUserExternalId.equals(entity.getUserExternalId())) {
                throw new UnauthorizedException("You don't have permission to update this support request");
            }
        }

        supportRequestMapper.updateEntityFromDto(dto, entity);
        entity = supportRequestRepository.save(entity);
        return supportRequestMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        SupportRequest entity = supportRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequest", "id", id));

        if (!securityService.isAdmin()) {
            String currentUserExternalId = securityService.getCurrentUserExternalId();
            if (currentUserExternalId == null || !currentUserExternalId.equals(entity.getUserExternalId())) {
                throw new UnauthorizedException("You don't have permission to delete this support request");
            }
        }

        supportRequestRepository.delete(entity);
    }

    @Transactional
    public SupportRequestDTO updateByExternalId(String externalId, SupportRequestDTO dto) {
        SupportRequest supportRequest = supportRequestRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequest", "externalId", externalId));
        
        if (!securityService.isAdmin()) {
            String currentUserExternalId = securityService.getCurrentUserExternalId();
            if (currentUserExternalId == null || !currentUserExternalId.equals(supportRequest.getUserExternalId())) {
                throw new UnauthorizedException("You don't have permission to update this support request");
            }
        }
        
        supportRequestMapper.updateEntityFromDto(dto, supportRequest);
        supportRequest = supportRequestRepository.save(supportRequest);
        log.info("Updated support request by externalId: {}", externalId);
        return supportRequestMapper.toDto(supportRequest);
    }

    @Transactional
    public void deleteByExternalId(String externalId) {
        SupportRequest supportRequest = supportRequestRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequest", "externalId", externalId));
        
        if (!securityService.isAdmin()) {
            String currentUserExternalId = securityService.getCurrentUserExternalId();
            if (currentUserExternalId == null || !currentUserExternalId.equals(supportRequest.getUserExternalId())) {
                throw new UnauthorizedException("You can only delete your own support requests");
            }
        }
        
        supportRequestRepository.delete(supportRequest);
        log.info("Deleted support request by externalId: {}", externalId);
    }
}




