package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.ServiceProductDTO;
import org.clickenrent.rentalservice.entity.ServiceProduct;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.ServiceProductMapper;
import org.clickenrent.rentalservice.repository.ServiceProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing ServiceProduct entities.
 */
@Service
@RequiredArgsConstructor
public class ServiceProductService {

    private final ServiceProductRepository serviceProductRepository;
    private final ServiceProductMapper serviceProductMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<ServiceProductDTO> getAllServiceProducts(Pageable pageable) {
        return serviceProductRepository.findAll(pageable)
                .map(serviceProductMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ServiceProductDTO getServiceProductById(Long id) {
        ServiceProduct serviceProduct = serviceProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProduct", "id", id));
        return serviceProductMapper.toDto(serviceProduct);
    }

    @Transactional
    public ServiceProductDTO createServiceProduct(ServiceProductDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create service products");
        }

        ServiceProduct serviceProduct = serviceProductMapper.toEntity(dto);
        serviceProduct = serviceProductRepository.save(serviceProduct);
        return serviceProductMapper.toDto(serviceProduct);
    }

    @Transactional
    public void deleteServiceProduct(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete service products");
        }

        ServiceProduct serviceProduct = serviceProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProduct", "id", id));
        serviceProductRepository.delete(serviceProduct);
    }
}


