package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.GlobalRoleDTO;
import org.clickenrent.authservice.entity.GlobalRole;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.GlobalRoleMapper;
import org.clickenrent.authservice.repository.GlobalRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing GlobalRole entities.
 */
@Service
@RequiredArgsConstructor
public class GlobalRoleService {
    
    private final GlobalRoleRepository globalRoleRepository;
    private final GlobalRoleMapper globalRoleMapper;
    
    @Transactional(readOnly = true)
    public List<GlobalRoleDTO> getAllGlobalRoles() {
        return globalRoleRepository.findAll().stream()
                .map(globalRoleMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public GlobalRoleDTO getGlobalRoleById(Long id) {
        GlobalRole globalRole = globalRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GlobalRole", "id", id));
        return globalRoleMapper.toDto(globalRole);
    }
    
    @Transactional
    public GlobalRoleDTO createGlobalRole(GlobalRoleDTO globalRoleDTO) {
        GlobalRole globalRole = globalRoleMapper.toEntity(globalRoleDTO);
        globalRole = globalRoleRepository.save(globalRole);
        return globalRoleMapper.toDto(globalRole);
    }
    
    @Transactional
    public GlobalRoleDTO updateGlobalRole(Long id, GlobalRoleDTO globalRoleDTO) {
        GlobalRole globalRole = globalRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GlobalRole", "id", id));
        
        globalRoleMapper.updateEntityFromDto(globalRoleDTO, globalRole);
        globalRole = globalRoleRepository.save(globalRole);
        return globalRoleMapper.toDto(globalRole);
    }
    
    @Transactional
    public void deleteGlobalRole(Long id) {
        GlobalRole globalRole = globalRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GlobalRole", "id", id));
        globalRoleRepository.delete(globalRole);
    }
}










