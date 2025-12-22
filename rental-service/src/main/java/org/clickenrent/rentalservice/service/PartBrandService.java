package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.contracts.auth.CompanyDTO;
import org.clickenrent.rentalservice.client.AuthServiceClient;
import org.clickenrent.rentalservice.dto.PartBrandDTO;
import org.clickenrent.rentalservice.entity.PartBrand;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.PartBrandMapper;
import org.clickenrent.rentalservice.repository.PartBrandRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartBrandService {

    private final PartBrandRepository partBrandRepository;
    private final PartBrandMapper partBrandMapper;
    private final SecurityService securityService;
    private final AuthServiceClient authServiceClient;

    @Transactional(readOnly = true)
    public Page<PartBrandDTO> getAllBrands(Pageable pageable) {
        return partBrandRepository.findAll(pageable)
                .map(partBrandMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<PartBrandDTO> getBrandsByCompany(Long companyId) {
        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(companyId)) {
            throw new UnauthorizedException("You don't have permission to view brands for this company");
        }

        return partBrandRepository.findByCompanyId(companyId).stream()
                .map(partBrandMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PartBrandDTO getBrandById(Long id) {
        PartBrand brand = partBrandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PartBrand", "id", id));
        return partBrandMapper.toDto(brand);
    }

    @Transactional
    public PartBrandDTO createBrand(PartBrandDTO dto) {
        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(dto.getCompanyId())) {
            throw new UnauthorizedException("You don't have permission to create brands for this company");
        }

        PartBrand brand = partBrandMapper.toEntity(dto);
        
        // DUAL-WRITE: Populate companyExternalId
        if (dto.getCompanyId() != null) {
            try {
                CompanyDTO company = authServiceClient.getCompanyById(dto.getCompanyId());
                brand.setCompanyId(dto.getCompanyId());
                brand.setCompanyExternalId(company.getExternalId());
                log.debug("Populated companyExternalId: {} for part brand", company.getExternalId());
            } catch (Exception e) {
                log.error("Failed to fetch company external ID for companyId: {}", dto.getCompanyId(), e);
                throw new RuntimeException("Failed to fetch company details", e);
            }
        }
        
        brand = partBrandRepository.save(brand);
        return partBrandMapper.toDto(brand);
    }

    @Transactional
    public PartBrandDTO updateBrand(Long id, PartBrandDTO dto) {
        PartBrand brand = partBrandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PartBrand", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(brand.getCompanyId())) {
            throw new UnauthorizedException("You don't have permission to update this brand");
        }

        partBrandMapper.updateEntityFromDto(dto, brand);
        brand = partBrandRepository.save(brand);
        return partBrandMapper.toDto(brand);
    }

    @Transactional
    public void deleteBrand(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete brands");
        }

        PartBrand brand = partBrandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PartBrand", "id", id));
        partBrandRepository.delete(brand);
    }
}




