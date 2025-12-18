package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.SupportRequestBikeIssueDTO;
import org.clickenrent.supportservice.entity.SupportRequestBikeIssue;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.SupportRequestBikeIssueMapper;
import org.clickenrent.supportservice.repository.SupportRequestBikeIssueRepository;
import org.clickenrent.supportservice.repository.SupportRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing SupportRequestBikeIssue junction entities.
 */
@Service
@RequiredArgsConstructor
public class SupportRequestBikeIssueService {

    private final SupportRequestBikeIssueRepository supportRequestBikeIssueRepository;
    private final SupportRequestBikeIssueMapper supportRequestBikeIssueMapper;
    private final SupportRequestRepository supportRequestRepository;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<SupportRequestBikeIssueDTO> getAll() {
        if (securityService.isAdmin()) {
            return supportRequestBikeIssueRepository.findAll().stream()
                    .map(supportRequestBikeIssueMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            throw new UnauthorizedException("Only administrators can view all support request issues");
        }
    }

    @Transactional(readOnly = true)
    public SupportRequestBikeIssueDTO getById(Long id) {
        SupportRequestBikeIssue entity = supportRequestBikeIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequestBikeIssue", "id", id));
        
        if (!securityService.isAdmin()) {
            Long userId = entity.getSupportRequest().getUserId();
            if (!securityService.hasAccessToUser(userId)) {
                throw new UnauthorizedException("You don't have permission to access this issue");
            }
        }
        
        return supportRequestBikeIssueMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<SupportRequestBikeIssueDTO> getBySupportRequestId(Long supportRequestId) {
        if (!securityService.isAdmin()) {
            var supportRequest = supportRequestRepository.findById(supportRequestId)
                    .orElseThrow(() -> new ResourceNotFoundException("SupportRequest", "id", supportRequestId));
            if (!securityService.hasAccessToUser(supportRequest.getUserId())) {
                throw new UnauthorizedException("You don't have permission to access these issues");
            }
        }
        
        return supportRequestBikeIssueRepository.findBySupportRequestId(supportRequestId).stream()
                .map(supportRequestBikeIssueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupportRequestBikeIssueDTO> getByBikeIssueId(Long bikeIssueId) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can view issues by bike issue ID");
        }
        
        return supportRequestBikeIssueRepository.findByBikeIssueId(bikeIssueId).stream()
                .map(supportRequestBikeIssueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupportRequestBikeIssueDTO create(SupportRequestBikeIssueDTO dto) {
        if (!securityService.isAdmin()) {
            var supportRequest = supportRequestRepository.findById(dto.getSupportRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException("SupportRequest", "id", dto.getSupportRequestId()));
            if (!securityService.hasAccessToUser(supportRequest.getUserId())) {
                throw new UnauthorizedException("You can only add issues to your own support requests");
            }
        }

        SupportRequestBikeIssue entity = supportRequestBikeIssueMapper.toEntity(dto);
        entity = supportRequestBikeIssueRepository.save(entity);
        return supportRequestBikeIssueMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        SupportRequestBikeIssue entity = supportRequestBikeIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequestBikeIssue", "id", id));

        if (!securityService.isAdmin()) {
            Long userId = entity.getSupportRequest().getUserId();
            if (!securityService.hasAccessToUser(userId)) {
                throw new UnauthorizedException("You don't have permission to delete this issue");
            }
        }

        supportRequestBikeIssueRepository.delete(entity);
    }
}


