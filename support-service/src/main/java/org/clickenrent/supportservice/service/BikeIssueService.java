package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeIssueDTO;
import org.clickenrent.supportservice.entity.BikeIssue;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeIssueMapper;
import org.clickenrent.supportservice.repository.BikeIssueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing BikeIssue entities.
 */
@Service
@RequiredArgsConstructor
public class BikeIssueService {

    private final BikeIssueRepository bikeIssueRepository;
    private final BikeIssueMapper bikeIssueMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeIssueDTO> getAll() {
        return bikeIssueRepository.findAll().stream()
                .map(bikeIssueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BikeIssueDTO getById(Long id) {
        BikeIssue entity = bikeIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeIssue", "id", id));
        return bikeIssueMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeIssueDTO getByExternalId(String externalId) {
        BikeIssue entity = bikeIssueRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeIssue", "externalId", externalId));
        return bikeIssueMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<BikeIssueDTO> getRootIssues() {
        return bikeIssueRepository.findByParentBikeIssueIsNull().stream()
                .map(bikeIssueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeIssueDTO> getSubIssues(Long parentId) {
        return bikeIssueRepository.findByParentBikeIssueId(parentId).stream()
                .map(bikeIssueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BikeIssueDTO create(BikeIssueDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bike issues");
        }

        BikeIssue entity = bikeIssueMapper.toEntity(dto);
        entity = bikeIssueRepository.save(entity);
        return bikeIssueMapper.toDto(entity);
    }

    @Transactional
    public BikeIssueDTO update(Long id, BikeIssueDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike issues");
        }

        BikeIssue entity = bikeIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeIssue", "id", id));

        bikeIssueMapper.updateEntityFromDto(dto, entity);
        entity = bikeIssueRepository.save(entity);
        return bikeIssueMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike issues");
        }

        BikeIssue entity = bikeIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeIssue", "id", id));
        bikeIssueRepository.delete(entity);
    }
}
