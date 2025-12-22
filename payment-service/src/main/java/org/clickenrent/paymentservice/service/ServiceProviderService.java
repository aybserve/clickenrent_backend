package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.ServiceProviderDTO;
import org.clickenrent.paymentservice.entity.ServiceProvider;
import org.clickenrent.paymentservice.exception.DuplicateResourceException;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.mapper.ServiceProviderMapper;
import org.clickenrent.paymentservice.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for ServiceProvider management
 */
@Service
@RequiredArgsConstructor
public class ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceProviderMapper serviceProviderMapper;

    @Transactional(readOnly = true)
    public List<ServiceProviderDTO> findAll() {
        return serviceProviderMapper.toDTOList(serviceProviderRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ServiceProviderDTO findById(Long id) {
        ServiceProvider provider = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProvider", "id", id));
        return serviceProviderMapper.toDTO(provider);
    }

    @Transactional(readOnly = true)
    public ServiceProviderDTO findByExternalId(String externalId) {
        ServiceProvider provider = serviceProviderRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProvider", "externalId", externalId));
        return serviceProviderMapper.toDTO(provider);
    }

    @Transactional(readOnly = true)
    public ServiceProviderDTO findByCode(String code) {
        ServiceProvider provider = serviceProviderRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProvider", "code", code));
        return serviceProviderMapper.toDTO(provider);
    }

    @Transactional
    public ServiceProviderDTO create(ServiceProviderDTO dto) {
        if (serviceProviderRepository.findByCode(dto.getCode()).isPresent()) {
            throw new DuplicateResourceException("ServiceProvider", "code", dto.getCode());
        }

        ServiceProvider provider = serviceProviderMapper.toEntity(dto);
        ServiceProvider savedProvider = serviceProviderRepository.save(provider);
        return serviceProviderMapper.toDTO(savedProvider);
    }

    @Transactional
    public ServiceProviderDTO update(Long id, ServiceProviderDTO dto) {
        ServiceProvider existingProvider = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProvider", "id", id));

        if (!existingProvider.getCode().equals(dto.getCode())) {
            if (serviceProviderRepository.findByCode(dto.getCode()).isPresent()) {
                throw new DuplicateResourceException("ServiceProvider", "code", dto.getCode());
            }
        }

        existingProvider.setCode(dto.getCode());
        existingProvider.setName(dto.getName());

        ServiceProvider updatedProvider = serviceProviderRepository.save(existingProvider);
        return serviceProviderMapper.toDTO(updatedProvider);
    }

    @Transactional
    public void delete(Long id) {
        if (!serviceProviderRepository.existsById(id)) {
            throw new ResourceNotFoundException("ServiceProvider", "id", id);
        }
        serviceProviderRepository.deleteById(id);
    }
}




