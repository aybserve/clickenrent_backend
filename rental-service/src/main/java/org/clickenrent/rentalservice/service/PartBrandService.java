package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class PartBrandService {

    private final PartBrandRepository partBrandRepository;
    private final PartBrandMapper partBrandMapper;
    private final SecurityService securityService;

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


