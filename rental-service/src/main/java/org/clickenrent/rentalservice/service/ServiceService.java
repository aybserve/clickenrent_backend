package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.ServiceDTO;
import org.clickenrent.rentalservice.entity.Service;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.ServiceMapper;
import org.clickenrent.rentalservice.repository.ServiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing Service entities.
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<ServiceDTO> getAllServices(Pageable pageable) {
        return serviceRepository.findAll(pageable)
                .map(serviceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ServiceDTO getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));
        return serviceMapper.toDto(service);
    }

    @Transactional
    public ServiceDTO createService(ServiceDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create services");
        }

        Service service = serviceMapper.toEntity(dto);
        service = serviceRepository.save(service);
        return serviceMapper.toDto(service);
    }

    @Transactional
    public ServiceDTO updateService(Long id, ServiceDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update services");
        }

        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));

        serviceMapper.updateEntityFromDto(dto, service);
        service = serviceRepository.save(service);
        return serviceMapper.toDto(service);
    }

    @Transactional
    public void deleteService(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete services");
        }

        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));
        serviceRepository.delete(service);
    }
}








