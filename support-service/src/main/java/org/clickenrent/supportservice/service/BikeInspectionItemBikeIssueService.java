package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.BikeInspectionItemBikeIssueDTO;
import org.clickenrent.supportservice.entity.BikeInspectionItemBikeIssue;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeInspectionItemBikeIssueMapper;
import org.clickenrent.supportservice.repository.BikeInspectionItemBikeIssueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing BikeInspectionItemBikeIssue entities.
 */
@Service
@RequiredArgsConstructor
public class BikeInspectionItemBikeIssueService {

    private final BikeInspectionItemBikeIssueRepository bikeInspectionItemBikeIssueRepository;
    private final BikeInspectionItemBikeIssueMapper bikeInspectionItemBikeIssueMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<BikeInspectionItemBikeIssueDTO> getAll() {
        return bikeInspectionItemBikeIssueRepository.findAll().stream()
                .map(bikeInspectionItemBikeIssueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BikeInspectionItemBikeIssueDTO getById(Long id) {
        BikeInspectionItemBikeIssue entity = bikeInspectionItemBikeIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemBikeIssue", "id", id));
        return bikeInspectionItemBikeIssueMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public BikeInspectionItemBikeIssueDTO getByExternalId(String externalId) {
        BikeInspectionItemBikeIssue entity = bikeInspectionItemBikeIssueRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemBikeIssue", "externalId", externalId));
        return bikeInspectionItemBikeIssueMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionItemBikeIssueDTO> getByBikeInspectionItemId(Long bikeInspectionItemId) {
        return bikeInspectionItemBikeIssueRepository.findByBikeInspectionItemId(bikeInspectionItemId).stream()
                .map(bikeInspectionItemBikeIssueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionItemBikeIssueDTO> getByBikeIssueId(Long bikeIssueId) {
        return bikeInspectionItemBikeIssueRepository.findByBikeIssueId(bikeIssueId).stream()
                .map(bikeInspectionItemBikeIssueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BikeInspectionItemBikeIssueDTO> getByCompanyExternalId(String companyExternalId) {
        return bikeInspectionItemBikeIssueRepository.findByCompanyExternalId(companyExternalId).stream()
                .map(bikeInspectionItemBikeIssueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BikeInspectionItemBikeIssueDTO create(BikeInspectionItemBikeIssueDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create bike inspection item bike issue links");
        }

        BikeInspectionItemBikeIssue entity = bikeInspectionItemBikeIssueMapper.toEntity(dto);
        entity.sanitizeForCreate();
        entity = bikeInspectionItemBikeIssueRepository.save(entity);
        return bikeInspectionItemBikeIssueMapper.toDto(entity);
    }

    @Transactional
    public BikeInspectionItemBikeIssueDTO update(Long id, BikeInspectionItemBikeIssueDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update bike inspection item bike issue links");
        }

        BikeInspectionItemBikeIssue entity = bikeInspectionItemBikeIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemBikeIssue", "id", id));

        bikeInspectionItemBikeIssueMapper.updateEntityFromDto(dto, entity);
        entity = bikeInspectionItemBikeIssueRepository.save(entity);
        return bikeInspectionItemBikeIssueMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete bike inspection item bike issue links");
        }

        BikeInspectionItemBikeIssue entity = bikeInspectionItemBikeIssueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeInspectionItemBikeIssue", "id", id));
        bikeInspectionItemBikeIssueRepository.delete(entity);
    }
}
