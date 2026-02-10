package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.UserAddressDTO;
import org.clickenrent.authservice.entity.Address;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserAddress;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.UserAddressMapper;
import org.clickenrent.authservice.repository.AddressRepository;
import org.clickenrent.authservice.repository.UserAddressRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing UserAddress entities.
 */
@Service
@RequiredArgsConstructor
public class UserAddressService {
    
    private final UserAddressRepository userAddressRepository;
    private final UserAddressMapper userAddressMapper;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    
    @Transactional(readOnly = true)
    public List<UserAddressDTO> getAllUserAddresses() {
        return userAddressRepository.findAll().stream()
                .map(userAddressMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public UserAddressDTO getUserAddressById(Long id) {
        UserAddress userAddress = userAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserAddress", "id", id));
        return userAddressMapper.toDto(userAddress);
    }
    
    @Transactional(readOnly = true)
    public UserAddressDTO getUserAddressByExternalId(String externalId) {
        UserAddress userAddress = userAddressRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("UserAddress", "externalId", externalId));
        return userAddressMapper.toDto(userAddress);
    }
    
    @Transactional(readOnly = true)
    public List<UserAddressDTO> getUserAddressesByUserId(Long userId) {
        return userAddressRepository.findByUserId(userId).stream()
                .map(userAddressMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UserAddressDTO> getUserAddressesByAddressId(Long addressId) {
        return userAddressRepository.findByAddressId(addressId).stream()
                .map(userAddressMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserAddressDTO createUserAddress(UserAddressDTO userAddressDTO) {
        UserAddress userAddress = userAddressMapper.toEntity(userAddressDTO);
        userAddress.sanitizeForCreate();
        
        // Set the user relationship
        if (userAddressDTO.getUserId() != null) {
            User user = userRepository.findById(userAddressDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userAddressDTO.getUserId()));
            userAddress.setUser(user);
        }
        
        // Set the address relationship
        if (userAddressDTO.getAddressId() != null) {
            Address address = addressRepository.findById(userAddressDTO.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "id", userAddressDTO.getAddressId()));
            userAddress.setAddress(address);
        }
        
        userAddress = userAddressRepository.save(userAddress);
        return userAddressMapper.toDto(userAddress);
    }
    
    @Transactional
    public UserAddressDTO updateUserAddress(Long id, UserAddressDTO userAddressDTO) {
        UserAddress userAddress = userAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserAddress", "id", id));
        
        userAddressMapper.updateEntityFromDto(userAddressDTO, userAddress);
        
        // Update user relationship if provided
        if (userAddressDTO.getUserId() != null) {
            User user = userRepository.findById(userAddressDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userAddressDTO.getUserId()));
            userAddress.setUser(user);
        }
        
        // Update address relationship if provided
        if (userAddressDTO.getAddressId() != null) {
            Address address = addressRepository.findById(userAddressDTO.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "id", userAddressDTO.getAddressId()));
            userAddress.setAddress(address);
        }
        
        userAddress = userAddressRepository.save(userAddress);
        return userAddressMapper.toDto(userAddress);
    }
    
    @Transactional
    public void deleteUserAddress(Long id) {
        UserAddress userAddress = userAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserAddress", "id", id));
        userAddressRepository.delete(userAddress);
    }
}









