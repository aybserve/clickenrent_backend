package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.SupportRequestGuideItemDTO;
import org.clickenrent.supportservice.entity.SupportRequestGuideItem;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.SupportRequestGuideItemMapper;
import org.clickenrent.supportservice.repository.SupportRequestGuideItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing SupportRequestGuideItem entities.
 */
@Service
@RequiredArgsConstructor
public class SupportRequestGuideItemService {

    private final SupportRequestGuideItemRepository supportRequestGuideItemRepository;
    private final SupportRequestGuideItemMapper supportRequestGuideItemMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<SupportRequestGuideItemDTO> getAll() {
        return supportRequestGuideItemRepository.findAll().stream()
                .map(supportRequestGuideItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupportRequestGuideItemDTO getById(Long id) {
        SupportRequestGuideItem entity = supportRequestGuideItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequestGuideItem", "id", id));
        return supportRequestGuideItemMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<SupportRequestGuideItemDTO> getByBikeIssueId(Long bikeIssueId) {
        return supportRequestGuideItemRepository.findByBikeIssueId(bikeIssueId).stream()
                .map(supportRequestGuideItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupportRequestGuideItemDTO> getByBikeIssueAndStatus(Long bikeIssueId, Long statusId) {
        return supportRequestGuideItemRepository
                .findByBikeIssueIdAndSupportRequestStatusIdOrderByItemIndexAsc(bikeIssueId, statusId).stream()
                .map(supportRequestGuideItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupportRequestGuideItemDTO create(SupportRequestGuideItemDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create guide items");
        }

        SupportRequestGuideItem entity = supportRequestGuideItemMapper.toEntity(dto);
        entity = supportRequestGuideItemRepository.save(entity);
        return supportRequestGuideItemMapper.toDto(entity);
    }

    @Transactional
    public SupportRequestGuideItemDTO update(Long id, SupportRequestGuideItemDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update guide items");
        }

        SupportRequestGuideItem entity = supportRequestGuideItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequestGuideItem", "id", id));

        supportRequestGuideItemMapper.updateEntityFromDto(dto, entity);
        entity = supportRequestGuideItemRepository.save(entity);
        return supportRequestGuideItemMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete guide items");
        }

        SupportRequestGuideItem entity = supportRequestGuideItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportRequestGuideItem", "id", id));
        supportRequestGuideItemRepository.delete(entity);
    }
}




