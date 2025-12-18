package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.FeedbackDTO;
import org.clickenrent.supportservice.entity.Feedback;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.FeedbackMapper;
import org.clickenrent.supportservice.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Feedback entities.
 */
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<FeedbackDTO> getAll() {
        if (securityService.isAdmin()) {
            return feedbackRepository.findAll().stream()
                    .map(feedbackMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            Long userId = securityService.getCurrentUserId();
            return feedbackRepository.findByUserId(userId).stream()
                    .map(feedbackMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public FeedbackDTO getById(Long id) {
        Feedback entity = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", id));
        
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(entity.getUserId())) {
            throw new UnauthorizedException("You don't have permission to access this feedback");
        }
        
        return feedbackMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public FeedbackDTO getByExternalId(String externalId) {
        Feedback entity = feedbackRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "externalId", externalId));
        
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(entity.getUserId())) {
            throw new UnauthorizedException("You don't have permission to access this feedback");
        }
        
        return feedbackMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<FeedbackDTO> getByUserId(Long userId) {
        if (!securityService.isAdmin() && !securityService.hasAccessToUser(userId)) {
            throw new UnauthorizedException("You don't have permission to access this user's feedback");
        }
        
        return feedbackRepository.findByUserId(userId).stream()
                .map(feedbackMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public FeedbackDTO create(FeedbackDTO dto) {
        Long currentUserId = securityService.getCurrentUserId();
        if (dto.getUserId() == null) {
            dto.setUserId(currentUserId);
        } else if (!securityService.isAdmin() && !currentUserId.equals(dto.getUserId())) {
            throw new UnauthorizedException("You can only create feedback for yourself");
        }

        Feedback entity = feedbackMapper.toEntity(dto);
        entity = feedbackRepository.save(entity);
        return feedbackMapper.toDto(entity);
    }

    @Transactional
    public FeedbackDTO update(Long id, FeedbackDTO dto) {
        Feedback entity = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToUser(entity.getUserId())) {
            throw new UnauthorizedException("You don't have permission to update this feedback");
        }

        feedbackMapper.updateEntityFromDto(dto, entity);
        entity = feedbackRepository.save(entity);
        return feedbackMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        Feedback entity = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToUser(entity.getUserId())) {
            throw new UnauthorizedException("You don't have permission to delete this feedback");
        }

        feedbackRepository.delete(entity);
    }
}


