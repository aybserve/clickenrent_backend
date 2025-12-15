package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.LocationRoleDTO;
import org.clickenrent.rentalservice.entity.LocationRole;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.LocationRoleMapper;
import org.clickenrent.rentalservice.repository.LocationRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationRoleService {

    private final LocationRoleRepository locationRoleRepository;
    private final LocationRoleMapper locationRoleMapper;

    @Transactional(readOnly = true)
    public List<LocationRoleDTO> getAllRoles() {
        return locationRoleRepository.findAll().stream()
                .map(locationRoleMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public LocationRoleDTO getRoleById(Long id) {
        LocationRole role = locationRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationRole", "id", id));
        return locationRoleMapper.toDto(role);
    }

    @Transactional
    public LocationRoleDTO createLocationRole(LocationRoleDTO locationRoleDTO) {
        LocationRole role = locationRoleMapper.toEntity(locationRoleDTO);
        LocationRole savedRole = locationRoleRepository.save(role);
        return locationRoleMapper.toDto(savedRole);
    }

    @Transactional
    public LocationRoleDTO updateLocationRole(Long id, LocationRoleDTO locationRoleDTO) {
        LocationRole role = locationRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationRole", "id", id));
        
        role.setName(locationRoleDTO.getName());
        
        LocationRole updatedRole = locationRoleRepository.save(role);
        return locationRoleMapper.toDto(updatedRole);
    }

    @Transactional
    public void deleteLocationRole(Long id) {
        LocationRole role = locationRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationRole", "id", id));
        locationRoleRepository.delete(role);
    }
}
