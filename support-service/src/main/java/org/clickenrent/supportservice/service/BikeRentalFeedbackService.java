package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.support.BikeRentalFeedbackDTO;
import org.clickenrent.supportservice.entity.BikeRentalFeedback;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeRentalFeedbackMapper;
import org.clickenrent.supportservice.repository.BikeRentalFeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing BikeRentalFeedback entities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BikeRentalFeedbackService {

    private final BikeRentalFeedbackRepository bikeRentalFeedbackRepository;
    private final BikeRentalFeedbackMapper bikeRentalFeedbackMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeRentalFeedbackDTO> getAll() {
        if (securityService.isAdmin()) {
            return bikeRentalFeedbackRepository.findAll().stream()
                    .map(bikeRentalFeedbackMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            String userExternalId = securityService.getCurrentUserExternalId();
            if (userExternalId == null) {
                log.error("Failed to get current user external ID from JWT");
                return List.of();
            }
            return bikeRentalFeedbackRepository.findByUserExternalId(userExternalId).stream()
                    .map(bikeRentalFeedbackMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public BikeRentalFeedbackDTO getById(Long id) {
        BikeRentalFeedback entity = bikeRentalFeedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRentalFeedback", "id", id));
        
        if (!securityService.isAdmin()) {
            String currentUserExternalId = securityService.getCurrentUserExternalId();
            if (currentUserExternalId == null || !currentUserExternalId.equals(entity.getUserExternalId())) {
                throw new UnauthorizedException("You don't have permission to access this feedback");
            }
        }
        
        return bikeRentalFeedbackMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeRentalFeedbackDTO getByExternalId(String externalId) {
        BikeRentalFeedback entity = bikeRentalFeedbackRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRentalFeedback", "externalId", externalId));
        
        if (!securityService.isAdmin()) {
            String currentUserExternalId = securityService.getCurrentUserExternalId();
            if (currentUserExternalId == null || !currentUserExternalId.equals(entity.getUserExternalId())) {
                throw new UnauthorizedException("You don't have permission to access this feedback");
            }
        }
        
        return bikeRentalFeedbackMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeRentalFeedbackDTO getByBikeRentalExternalId(String bikeRentalExternalId) {
        BikeRentalFeedback entity = bikeRentalFeedbackRepository.findByBikeRentalExternalId(bikeRentalExternalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRentalFeedback", "bikeRentalExternalId", bikeRentalExternalId));
        
        if (!securityService.isAdmin()) {
            String currentUserExternalId = securityService.getCurrentUserExternalId();
            if (currentUserExternalId == null || !currentUserExternalId.equals(entity.getUserExternalId())) {
                throw new UnauthorizedException("You don't have permission to access this feedback");
            }
        }
        
        return bikeRentalFeedbackMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<BikeRentalFeedbackDTO> getByUserExternalId(String userExternalId) {
        if (!securityService.isAdmin()) {
            String currentUserExternalId = securityService.getCurrentUserExternalId();
            if (currentUserExternalId == null || !currentUserExternalId.equals(userExternalId)) {
                throw new UnauthorizedException("You don't have permission to access this user's feedback");
            }
        }
        
        return bikeRentalFeedbackRepository.findByUserExternalId(userExternalId).stream()
                .map(bikeRentalFeedbackMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BikeRentalFeedbackDTO create(BikeRentalFeedbackDTO dto) {
        String currentUserExternalId = securityService.getCurrentUserExternalId();
        if (currentUserExternalId == null) {
            throw new RuntimeException("Failed to get current user external ID from JWT");
        }
        
        // Set userExternalId if not provided or verify if provided
        if (dto.getUserExternalId() == null) {
            dto.setUserExternalId(currentUserExternalId);
        } else if (!securityService.isAdmin() && !currentUserExternalId.equals(dto.getUserExternalId())) {
            throw new UnauthorizedException("You can only create feedback for yourself");
        }

        BikeRentalFeedback entity = bikeRentalFeedbackMapper.toEntity(dto);
        entity.sanitizeForCreate();
        entity = bikeRentalFeedbackRepository.save(entity);
        return bikeRentalFeedbackMapper.toDto(entity);
    }

    @Transactional
    public BikeRentalFeedbackDTO update(Long id, BikeRentalFeedbackDTO dto) {
        BikeRentalFeedback entity = bikeRentalFeedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRentalFeedback", "id", id));

        if (!securityService.isAdmin()) {
            String currentUserExternalId = securityService.getCurrentUserExternalId();
            if (currentUserExternalId == null || !currentUserExternalId.equals(entity.getUserExternalId())) {
                throw new UnauthorizedException("You don't have permission to update this feedback");
            }
        }

        bikeRentalFeedbackMapper.updateEntityFromDto(dto, entity);
        entity = bikeRentalFeedbackRepository.save(entity);
        return bikeRentalFeedbackMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        BikeRentalFeedback entity = bikeRentalFeedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRentalFeedback", "id", id));

        if (!securityService.isAdmin()) {
            String currentUserExternalId = securityService.getCurrentUserExternalId();
            if (currentUserExternalId == null || !currentUserExternalId.equals(entity.getUserExternalId())) {
                throw new UnauthorizedException("You don't have permission to delete this feedback");
            }
        }

        bikeRentalFeedbackRepository.delete(entity);
    }
}








