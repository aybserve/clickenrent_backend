package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.UserCompanyDTO;
import org.clickenrent.authservice.dto.UserCompanyDetailDTO;
import org.clickenrent.authservice.entity.Company;
import org.clickenrent.authservice.entity.CompanyRole;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserCompany;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.exception.UnauthorizedException;
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
    private final SecurityService securityService;
    
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
    public List<UserCompanyDetailDTO> getUserCompanies(Long userId) {
        // Security check: Only admins or the user themselves can view their companies
        Long currentUserId = securityService.getCurrentUserId();
        if (!securityService.isAdmin() && (currentUserId == null || !currentUserId.equals(userId))) {
            throw new UnauthorizedException("You don't have permission to view this user's companies");
        }
        
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return userCompanyRepository.findByUserId(userId).stream()
                .map(userCompanyMapper::toDetailDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UserCompanyDetailDTO> getCompanyUsers(Long companyId) {
        // Security check: Must have access to this company
        if (!securityService.hasAccessToCompany(companyId)) {
            throw new UnauthorizedException("You don't have permission to view users in this company");
        }
        
        // Verify company exists
        companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
        
        // Use optimized query instead of loading all records
        return userCompanyRepository.findByCompanyId(companyId).stream()
                .map(userCompanyMapper::toDetailDto)
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

