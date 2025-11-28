package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.UserDTO;
import org.clickenrent.authservice.entity.Language;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.UserMapper;
import org.clickenrent.authservice.repository.LanguageRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing User entities.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toDto(user);
    }
    
    @Transactional(readOnly = true)
    public UserDTO getUserByExternalId(String externalId) {
        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "externalId", externalId));
        return userMapper.toDto(user);
    }
    
    @Transactional
    public UserDTO createUser(UserDTO userDTO, String password) {
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(password));
        
        if (userDTO.getLanguageId() != null) {
            Language language = languageRepository.findById(userDTO.getLanguageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Language", "id", userDTO.getLanguageId()));
            user.setLanguage(language);
        }
        
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        if (user.getIsDeleted() == null) {
            user.setIsDeleted(false);
        }
        
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }
    
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        userMapper.updateEntityFromDto(userDTO, user);
        
        if (userDTO.getLanguageId() != null) {
            Language language = languageRepository.findById(userDTO.getLanguageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Language", "id", userDTO.getLanguageId()));
            user.setLanguage(language);
        }
        
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setIsDeleted(true);
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    @Transactional
    public UserDTO activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setIsActive(true);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }
    
    @Transactional
    public UserDTO deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setIsActive(false);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }
}

