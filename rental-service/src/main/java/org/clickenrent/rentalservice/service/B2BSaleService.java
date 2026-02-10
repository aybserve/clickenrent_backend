package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleDTO;
import org.clickenrent.rentalservice.entity.B2BSale;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.B2BSaleMapper;
import org.clickenrent.rentalservice.repository.B2BSaleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class B2BSaleService {

    private final B2BSaleRepository b2bSaleRepository;
    private final B2BSaleMapper b2bSaleMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<B2BSaleDTO> getAllSales(Pageable pageable) {
        if (securityService.isAdmin()) {
            return b2bSaleRepository.findAll(pageable)
                    .map(b2bSaleMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all B2B sales");
    }

    @Transactional(readOnly = true)
    public List<B2BSaleDTO> getSalesByLocation(Long locationId) {
        // Access control - admin or users with access to the location
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to view sales for this location");
        }

        return b2bSaleRepository.findByLocationId(locationId).stream()
                .map(b2bSaleMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public B2BSaleDTO getSaleById(Long id) {
        B2BSale sale = b2bSaleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSale", "id", id));

        // Check access
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to view this sale");
        }

        return b2bSaleMapper.toDto(sale);
    }

    @Transactional
    public B2BSaleDTO createSale(B2BSaleDTO dto) {
        // Validate user has admin access
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to create sales");
        }

        B2BSale sale = b2bSaleMapper.toEntity(dto);
        sale.sanitizeForCreate();
        sale = b2bSaleRepository.save(sale);
        return b2bSaleMapper.toDto(sale);
    }

    @Transactional
    public B2BSaleDTO updateSale(Long id, B2BSaleDTO dto) {
        B2BSale sale = b2bSaleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSale", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to update this sale");
        }

        b2bSaleMapper.updateEntityFromDto(dto, sale);
        sale = b2bSaleRepository.save(sale);
        return b2bSaleMapper.toDto(sale);
    }

    @Transactional
    public void deleteSale(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete B2B sales");
        }

        B2BSale sale = b2bSaleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSale", "id", id));
        b2bSaleRepository.delete(sale);
    }

    @Transactional(readOnly = true)
    public B2BSaleDTO findByExternalId(String externalId) {
        B2BSale sale = b2bSaleRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSale", "externalId", externalId));

        // Check access
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to view this sale");
        }

        return b2bSaleMapper.toDto(sale);
    }

    @Transactional(readOnly = true)
    public boolean existsByExternalId(String externalId) {
        return b2bSaleRepository.existsByExternalId(externalId);
    }
}
