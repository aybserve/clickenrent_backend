package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.HubImageDTO;
import org.clickenrent.rentalservice.entity.Hub;
import org.clickenrent.rentalservice.entity.HubImage;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.HubImageMapper;
import org.clickenrent.rentalservice.repository.HubImageRepository;
import org.clickenrent.rentalservice.repository.HubRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing HubImage entities.
 */
@Service
@RequiredArgsConstructor
public class HubImageService {

    private final HubImageRepository hubImageRepository;
    private final HubRepository hubRepository;
    private final HubImageMapper hubImageMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<HubImageDTO> getImagesByHub(Long hubId) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new ResourceNotFoundException("Hub", "id", hubId));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(hub.getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to view images for this hub");
        }

        return hubImageRepository.findByHub(hub).stream()
                .map(hubImageMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public HubImageDTO getImageById(Long id) {
        HubImage hubImage = hubImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HubImage", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(hubImage.getHub().getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to view this image");
        }

        return hubImageMapper.toDto(hubImage);
    }

    @Transactional
    public HubImageDTO createImage(HubImageDTO dto) {
        Hub hub = hubRepository.findById(dto.getHubId())
                .orElseThrow(() -> new ResourceNotFoundException("Hub", "id", dto.getHubId()));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(hub.getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to create images for this hub");
        }

        HubImage hubImage = hubImageMapper.toEntity(dto);
        hubImage.sanitizeForCreate();
        hubImage = hubImageRepository.save(hubImage);
        return hubImageMapper.toDto(hubImage);
    }

    @Transactional
    public HubImageDTO updateImage(Long id, HubImageDTO dto) {
        HubImage hubImage = hubImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HubImage", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(hubImage.getHub().getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to update this image");
        }

        hubImageMapper.updateEntityFromDto(dto, hubImage);
        hubImage = hubImageRepository.save(hubImage);
        return hubImageMapper.toDto(hubImage);
    }

    @Transactional
    public void deleteImage(Long id) {
        HubImage hubImage = hubImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HubImage", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(hubImage.getHub().getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to delete this image");
        }

        hubImageRepository.delete(hubImage);
    }

    @Transactional(readOnly = true)
    public HubImageDTO getImageByExternalId(String externalId) {
        HubImage hubImage = hubImageRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("HubImage", "externalId", externalId));
        return hubImageMapper.toDto(hubImage);
    }
}




