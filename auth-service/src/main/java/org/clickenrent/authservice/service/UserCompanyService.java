package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.UserCompanyDTO;
import org.clickenrent.authservice.entity.Company;
import org.clickenrent.authservice.entity.CompanyRole;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserCompany;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.UserCompanyMapper;
import org.clickenrent.authservice.repository.CompanyRepository;
import org.clickenrent.authservice.repository.CompanyRoleRepository;
import org.clickenrent.authservice.repository.UserCompanyRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing UserCompany relationships.
 */
@Service
@RequiredArgsConstructor
public class UserCompanyService {
    
    private final UserCompanyRepository userCompanyRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyRoleRepository companyRoleRepository;
    private final UserCompanyMapper userCompanyMapper;
    
    @Transactional
    public UserCompanyDTO assignUserToCompany(Long userId, Long companyId, Long companyRoleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
        
        CompanyRole companyRole = companyRoleRepository.findById(companyRoleId)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyRole", "id", companyRoleId));
        
        UserCompany userCompany = UserCompany.builder()
                .user(user)
                .company(company)
                .companyRole(companyRole)
                .build();
        
        userCompany = userCompanyRepository.save(userCompany);
        return userCompanyMapper.toDto(userCompany);
    }
    
    @Transactional(readOnly = true)
    public List<UserCompanyDTO> getUserCompanies(Long userId) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return userCompanyRepository.findAll().stream()
                .filter(uc -> uc.getUser().getId().equals(userId))
                .map(userCompanyMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UserCompanyDTO> getCompanyUsers(Long companyId) {
        // Verify company exists
        companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
        
        return userCompanyRepository.findAll().stream()
                .filter(uc -> uc.getCompany().getId().equals(companyId))
                .map(userCompanyMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserCompanyDTO updateUserCompanyRole(Long userCompanyId, Long companyRoleId) {
        UserCompany userCompany = userCompanyRepository.findById(userCompanyId)
                .orElseThrow(() -> new ResourceNotFoundException("UserCompany", "id", userCompanyId));
        
        CompanyRole companyRole = companyRoleRepository.findById(companyRoleId)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyRole", "id", companyRoleId));
        
        userCompany.setCompanyRole(companyRole);
        userCompany = userCompanyRepository.save(userCompany);
        return userCompanyMapper.toDto(userCompany);
    }
    
    @Transactional
    public void removeUserFromCompany(Long userCompanyId) {
        UserCompany userCompany = userCompanyRepository.findById(userCompanyId)
                .orElseThrow(() -> new ResourceNotFoundException("UserCompany", "id", userCompanyId));
        userCompanyRepository.delete(userCompany);
    }
}

