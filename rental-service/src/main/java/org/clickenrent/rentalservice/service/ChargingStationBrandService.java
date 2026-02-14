package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.rentalservice.dto.ChargingStationBrandDTO;
import org.clickenrent.rentalservice.entity.ChargingStationBrand;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.ChargingStationBrandMapper;
import org.clickenrent.rentalservice.repository.ChargingStationBrandRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargingStationBrandService {

    private final ChargingStationBrandRepository chargingStationBrandRepository;
    private final ChargingStationBrandMapper chargingStationBrandMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<ChargingStationBrandDTO> getAllBrands(Pageable pageable) {
        return chargingStationBrandRepository.findAll(pageable)
                .map(chargingStationBrandMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ChargingStationBrandDTO> getBrandsByCompanyExternalId(String companyExternalId) {
        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(companyExternalId)) {
            throw new UnauthorizedException("You don't have permission to view brands for this company");
        }

        return chargingStationBrandRepository.findByCompanyExternalId(companyExternalId).stream()
                .map(chargingStationBrandMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChargingStationBrandDTO getBrandById(Long id) {
        ChargingStationBrand brand = chargingStationBrandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChargingStationBrand", "id", id));
        return chargingStationBrandMapper.toDto(brand);
    }

    @Transactional
    public ChargingStationBrandDTO createBrand(ChargingStationBrandDTO dto) {
        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(dto.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to create brands for this company");
        }

        ChargingStationBrand brand = chargingStationBrandMapper.toEntity(dto);
        brand.sanitizeForCreate();
        brand = chargingStationBrandRepository.save(brand);
        return chargingStationBrandMapper.toDto(brand);
    }

    @Transactional
    public ChargingStationBrandDTO updateBrand(Long id, ChargingStationBrandDTO dto) {
        ChargingStationBrand brand = chargingStationBrandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChargingStationBrand", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(brand.getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to update this brand");
        }

        chargingStationBrandMapper.updateEntityFromDto(dto, brand);
        brand = chargingStationBrandRepository.save(brand);
        return chargingStationBrandMapper.toDto(brand);
    }

    @Transactional
    public void deleteBrand(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete brands");
        }

        ChargingStationBrand brand = chargingStationBrandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChargingStationBrand", "id", id));
        chargingStationBrandRepository.delete(brand);
    }

    @Transactional(readOnly = true)
    public ChargingStationBrandDTO getChargingStationBrandByExternalId(String externalId) {
        ChargingStationBrand brand = chargingStationBrandRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("ChargingStationBrand", "externalId", externalId));
        return chargingStationBrandMapper.toDto(brand);
    }
}




