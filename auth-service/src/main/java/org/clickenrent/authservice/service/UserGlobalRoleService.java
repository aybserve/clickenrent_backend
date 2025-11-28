package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.UserGlobalRoleDTO;
import org.clickenrent.authservice.entity.GlobalRole;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserGlobalRole;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.UserGlobalRoleMapper;
import org.clickenrent.authservice.repository.GlobalRoleRepository;
import org.clickenrent.authservice.repository.UserGlobalRoleRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing UserGlobalRole relationships.
 */
@Service
@RequiredArgsConstructor
public class UserGlobalRoleService {
    
    private final UserGlobalRoleRepository userGlobalRoleRepository;
    private final UserRepository userRepository;
    private final GlobalRoleRepository globalRoleRepository;
    private final UserGlobalRoleMapper userGlobalRoleMapper;
    
    @Transactional
    public UserGlobalRoleDTO assignGlobalRoleToUser(Long userId, Long globalRoleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        GlobalRole globalRole = globalRoleRepository.findById(globalRoleId)
                .orElseThrow(() -> new ResourceNotFoundException("GlobalRole", "id", globalRoleId));
        
        UserGlobalRole userGlobalRole = UserGlobalRole.builder()
                .user(user)
                .globalRole(globalRole)
                .build();
        
        userGlobalRole = userGlobalRoleRepository.save(userGlobalRole);
        return userGlobalRoleMapper.toDto(userGlobalRole);
    }
    
    @Transactional(readOnly = true)
    public List<UserGlobalRoleDTO> getUserGlobalRoles(Long userId) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return userGlobalRoleRepository.findAll().stream()
                .filter(ugr -> ugr.getUser().getId().equals(userId))
                .map(userGlobalRoleMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void removeGlobalRoleFromUser(Long userGlobalRoleId) {
        UserGlobalRole userGlobalRole = userGlobalRoleRepository.findById(userGlobalRoleId)
                .orElseThrow(() -> new ResourceNotFoundException("UserGlobalRole", "id", userGlobalRoleId));
        userGlobalRoleRepository.delete(userGlobalRole);
    }
}

