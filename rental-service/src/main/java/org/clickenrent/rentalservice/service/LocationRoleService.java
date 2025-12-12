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
}
