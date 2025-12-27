package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.CompanyRoleDTO;
import org.clickenrent.authservice.entity.CompanyRole;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.CompanyRoleMapper;
import org.clickenrent.authservice.repository.CompanyRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing CompanyRole entities.
 */
@Service
@RequiredArgsConstructor
public class CompanyRoleService {
    
    private final CompanyRoleRepository companyRoleRepository;
    private final CompanyRoleMapper companyRoleMapper;
    
    @Transactional(readOnly = true)
    public List<CompanyRoleDTO> getAllCompanyRoles() {
        return companyRoleRepository.findAll().stream()
                .map(companyRoleMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CompanyRoleDTO getCompanyRoleById(Long id) {
        CompanyRole companyRole = companyRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyRole", "id", id));
        return companyRoleMapper.toDto(companyRole);
    }
    
    @Transactional
    public CompanyRoleDTO createCompanyRole(CompanyRoleDTO companyRoleDTO) {
        CompanyRole companyRole = companyRoleMapper.toEntity(companyRoleDTO);
        companyRole = companyRoleRepository.save(companyRole);
        return companyRoleMapper.toDto(companyRole);
    }
    
    @Transactional
    public CompanyRoleDTO updateCompanyRole(Long id, CompanyRoleDTO companyRoleDTO) {
        CompanyRole companyRole = companyRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyRole", "id", id));
        
        companyRoleMapper.updateEntityFromDto(companyRoleDTO, companyRole);
        companyRole = companyRoleRepository.save(companyRole);
        return companyRoleMapper.toDto(companyRole);
    }
    
    @Transactional
    public void deleteCompanyRole(Long id) {
        CompanyRole companyRole = companyRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyRole", "id", id));
        companyRoleRepository.delete(companyRole);
    }
}









