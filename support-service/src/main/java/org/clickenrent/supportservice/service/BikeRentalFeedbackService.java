package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeRentalFeedbackDTO;
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
            Long userId = securityService.getCurrentUserId();
            return bikeRentalFeedbackRepository.findByUserId(userId).stream()
                    .map(bikeRentalFeedbackMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public BikeRentalFeedbackDTO getById(Long id) {
        BikeRentalFeedback entity = bikeRentalFeedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRentalFeedback", "id", id));
        
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(entity.getUserId())) {
            throw new UnauthorizedException("You don't have permission to access this feedback");
        }
        
        return bikeRentalFeedbackMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeRentalFeedbackDTO getByBikeRentalId(Long bikeRentalId) {
        BikeRentalFeedback entity = bikeRentalFeedbackRepository.findByBikeRentalId(bikeRentalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRentalFeedback", "bikeRentalId", bikeRentalId));
        
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(entity.getUserId())) {
            throw new UnauthorizedException("You don't have permission to access this feedback");
        }
        
        return bikeRentalFeedbackMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<BikeRentalFeedbackDTO> getByUserId(Long userId) {
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(userId)) {
            throw new UnauthorizedException("You don't have permission to access this user's feedback");
        }
        
        return bikeRentalFeedbackRepository.findByUserId(userId).stream()
                .map(bikeRentalFeedbackMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BikeRentalFeedbackDTO create(BikeRentalFeedbackDTO dto) {
        Long currentUserId = securityService.getCurrentUserId();
        if (dto.getUserId() == null) {
            dto.setUserId(currentUserId);
        } else if (!securityService.isAdmin() && !currentUserId.equals(dto.getUserId())) {
            throw new UnauthorizedException("You can only create feedback for yourself");
        }

        BikeRentalFeedback entity = bikeRentalFeedbackMapper.toEntity(dto);
        entity = bikeRentalFeedbackRepository.save(entity);
        return bikeRentalFeedbackMapper.toDto(entity);
    }

    @Transactional
    public BikeRentalFeedbackDTO update(Long id, BikeRentalFeedbackDTO dto) {
        BikeRentalFeedback entity = bikeRentalFeedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRentalFeedback", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToUser(entity.getUserId())) {
            throw new UnauthorizedException("You don't have permission to update this feedback");
        }

        bikeRentalFeedbackMapper.updateEntityFromDto(dto, entity);
        entity = bikeRentalFeedbackRepository.save(entity);
        return bikeRentalFeedbackMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        BikeRentalFeedback entity = bikeRentalFeedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeRentalFeedback", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToUser(entity.getUserId())) {
            throw new UnauthorizedException("You don't have permission to delete this feedback");
        }

        bikeRentalFeedbackRepository.delete(entity);
    }
}

