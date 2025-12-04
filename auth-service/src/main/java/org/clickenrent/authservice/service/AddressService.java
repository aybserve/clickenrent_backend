package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.AddressDTO;
import org.clickenrent.authservice.entity.Address;
import org.clickenrent.authservice.entity.City;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.AddressMapper;
import org.clickenrent.authservice.repository.AddressRepository;
import org.clickenrent.authservice.repository.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Address entities.
 */
@Service
@RequiredArgsConstructor
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final CityRepository cityRepository;
    
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
        return addressMapper.toDto(address);
    }
    
    @Transactional
    public AddressDTO updateAddress(Long id, AddressDTO addressDTO) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
        
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
        addressRepository.delete(address);
    }
}

