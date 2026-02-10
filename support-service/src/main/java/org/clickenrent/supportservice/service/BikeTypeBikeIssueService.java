package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeTypeBikeIssueDTO;
import org.clickenrent.supportservice.entity.BikeTypeBikeIssue;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeTypeBikeIssueMapper;
import org.clickenrent.supportservice.repository.BikeTypeBikeIssueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing BikeTypeBikeIssue junction entities.
 */
@Service
@RequiredArgsConstructor
public class BikeTypeBikeIssueService {

    private final BikeTypeBikeIssueRepository bikeTypeBikeIssueRepository;
    private final BikeTypeBikeIssueMapper bikeTypeBikeIssueMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeTypeBikeIssueDTO> getAll() {
        return bikeTypeBikeIssueRepository.findAll().stream()
                .map(bikeTypeBikeIssueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BikeTypeBikeIssueDTO getById(Long id) {
        BikeTypeBikeIssue entity = bikeTypeBikeIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeTypeBikeIssue", "id", id));
        return bikeTypeBikeIssueMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeTypeBikeIssueDTO getByExternalId(String externalId) {
        BikeTypeBikeIssue entity = bikeTypeBikeIssueRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeTypeBikeIssue", "externalId", externalId));
        return bikeTypeBikeIssueMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<BikeTypeBikeIssueDTO> getByBikeTypeExternalId(String bikeTypeExternalId) {
        return bikeTypeBikeIssueRepository.findByBikeTypeExternalId(bikeTypeExternalId).stream()
                .map(bikeTypeBikeIssueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeTypeBikeIssueDTO> getByBikeIssueId(Long bikeIssueId) {
        return bikeTypeBikeIssueRepository.findByBikeIssueId(bikeIssueId).stream()
                .map(bikeTypeBikeIssueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BikeTypeBikeIssueDTO create(BikeTypeBikeIssueDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can link bike types to issues");
        }

        BikeTypeBikeIssue entity = bikeTypeBikeIssueMapper.toEntity(dto);
        // Sanitize server-managed fields
        entity.setId(null);
        entity.setExternalId(null);
        entity = bikeTypeBikeIssueRepository.save(entity);
        return bikeTypeBikeIssueMapper.toDto(entity);
    }

    @Transactional
    public BikeTypeBikeIssueDTO update(Long id, BikeTypeBikeIssueDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike type issue links");
        }

        BikeTypeBikeIssue entity = bikeTypeBikeIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeTypeBikeIssue", "id", id));

        bikeTypeBikeIssueMapper.updateEntityFromDto(dto, entity);
        entity = bikeTypeBikeIssueRepository.save(entity);
        return bikeTypeBikeIssueMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike type issue links");
        }

        BikeTypeBikeIssue entity = bikeTypeBikeIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeTypeBikeIssue", "id", id));
        bikeTypeBikeIssueRepository.delete(entity);
    }
}








