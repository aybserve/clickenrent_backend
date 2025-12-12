package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.AddressDTO;
import org.clickenrent.authservice.entity.Address;
import org.clickenrent.authservice.entity.City;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserAddress;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.AddressMapper;
import org.clickenrent.authservice.repository.AddressRepository;
import org.clickenrent.authservice.repository.CityRepository;
import org.clickenrent.authservice.repository.UserAddressRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Address entities.
 * 
 * Security Implementation (Defense in Depth):
 * - Primary: Controller layer uses @PreAuthorize with SpEL expressions
 * - Secondary: Service layer validates access (protects internal service-to-service calls)
 * 
 * This dual-layer approach ensures security even if services are called directly.
 */
@Service
@RequiredArgsConstructor
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final CityRepository cityRepository;
    private final SecurityService securityService;
    private final UserAddressRepository userAddressRepository;
    
    @Transactional(readOnly = true)
    public List<AddressDTO> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public AddressDTO getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
        
        // Defense in depth: Secondary access check (primary check is in controller @PreAuthorize)
        if (!securityService.hasAccessToAddress(id)) {
            throw new AccessDeniedException("You don't have permission to access this address");
        }
        
        return addressMapper.toDto(address);
    }
    
    @Transactional(readOnly = true)
    public List<AddressDTO> getAddressesByCityId(Long cityId) {
        return addressRepository.findByCityId(cityId).stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public AddressDTO createAddress(AddressDTO addressDTO) {
        Address address = addressMapper.toEntity(addressDTO);
        
        // Set the city relationship
        if (addressDTO.getCityId() != null) {
            City city = cityRepository.findById(addressDTO.getCityId())
                    .orElseThrow(() -> new ResourceNotFoundException("City", "id", addressDTO.getCityId()));
            address.setCity(city);
        }
        
        address = addressRepository.save(address);
        
        // If user is B2B or CUSTOMER, automatically associate the address with them
        if (!securityService.isAdmin()) {
            User currentUser = securityService.getCurrentUser();
            if (currentUser != null) {
                UserAddress userAddress = UserAddress.builder()
                        .user(currentUser)
                        .address(address)
                        .build();
                userAddressRepository.save(userAddress);
            }
        }
        
        return addressMapper.toDto(address);
    }
    
    @Transactional
    public AddressDTO updateAddress(Long id, AddressDTO addressDTO) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
        
        // Defense in depth: Secondary access check (primary check is in controller @PreAuthorize)
        if (!securityService.hasAccessToAddress(id)) {
            throw new AccessDeniedException("You don't have permission to update this address");
        }
        
        addressMapper.updateEntityFromDto(addressDTO, address);
        
        // Update city relationship if provided
        if (addressDTO.getCityId() != null) {
            City city = cityRepository.findById(addressDTO.getCityId())
                    .orElseThrow(() -> new ResourceNotFoundException("City", "id", addressDTO.getCityId()));
            address.setCity(city);
        }
        
        address = addressRepository.save(address);
        return addressMapper.toDto(address);
    }
    
    @Transactional
    public void deleteAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
        
        // Defense in depth: Secondary access check (primary check is in controller @PreAuthorize)
        if (!securityService.hasAccessToAddress(id)) {
            throw new AccessDeniedException("You don't have permission to delete this address");
        }
        
        addressRepository.delete(address);
    }
}

