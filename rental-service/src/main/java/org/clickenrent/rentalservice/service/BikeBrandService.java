package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.rentalservice.dto.BikeBrandDTO;
import org.clickenrent.rentalservice.entity.BikeBrand;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.BikeBrandMapper;
import org.clickenrent.rentalservice.repository.BikeBrandRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BikeBrandService {

    private final BikeBrandRepository bikeBrandRepository;
    private final BikeBrandMapper bikeBrandMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<BikeBrandDTO> getAllBikeBrands(Pageable pageable) {
        return bikeBrandRepository.findAll(pageable)
                .map(bikeBrandMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<BikeBrandDTO> getBikeBrandsByCompanyExternalId(String companyExternalId) {
        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
            throw new UnauthorizedException("You don't have permission to view brands for this company");
        }

        return bikeBrandRepository.findByCompanyExternalId(companyExternalId).stream()
                .map(bikeBrandMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BikeBrandDTO getBikeBrandById(Long id) {
        BikeBrand bikeBrand = bikeBrandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeBrand", "id", id));
        return bikeBrandMapper.toDto(bikeBrand);
    }

    @Transactional
    public BikeBrandDTO createBikeBrand(BikeBrandDTO dto) {
        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(dto.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to create brands for this company");
        }

        BikeBrand bikeBrand = bikeBrandMapper.toEntity(dto);
        bikeBrand = bikeBrandRepository.save(bikeBrand);
        return bikeBrandMapper.toDto(bikeBrand);
    }

    @Transactional
    public BikeBrandDTO updateBikeBrand(Long id, BikeBrandDTO dto) {
        BikeBrand bikeBrand = bikeBrandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeBrand", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(bikeBrand.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to update this brand");
        }

        bikeBrandMapper.updateEntityFromDto(dto, bikeBrand);
        bikeBrand = bikeBrandRepository.save(bikeBrand);
        return bikeBrandMapper.toDto(bikeBrand);
    }

    @Transactional
    public void deleteBikeBrand(Long id) {
        BikeBrand bikeBrand = bikeBrandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BikeBrand", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete brands");
        }

        bikeBrandRepository.delete(bikeBrand);
    }
}




