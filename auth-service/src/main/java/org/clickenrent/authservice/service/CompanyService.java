package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.CompanyDTO;
import org.clickenrent.authservice.entity.Company;
import org.clickenrent.authservice.entity.CompanyType;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.exception.UnauthorizedException;
import org.clickenrent.authservice.mapper.CompanyMapper;
import org.clickenrent.authservice.repository.CompanyRepository;
import org.clickenrent.authservice.repository.CompanyTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing Company entities.
 */
@Service
@RequiredArgsConstructor
public class CompanyService {
    
    private final CompanyRepository companyRepository;
    private final CompanyTypeRepository companyTypeRepository;
    private final CompanyMapper companyMapper;
    private final SecurityService securityService;
    
    @Transactional(readOnly = true)
    public Page<CompanyDTO> getAllCompanies(Pageable pageable) {
        // Admin can see all companies
        if (securityService.isAdmin()) {
            return companyRepository.findAll(pageable)
                    .map(companyMapper::toDto);
        }
        
        // B2B can only see their companies
        if (securityService.isB2B()) {
            List<Long> accessibleCompanyIds = securityService.getAccessibleCompanyIds();
            if (accessibleCompanyIds == null || accessibleCompanyIds.isEmpty()) {
                return Page.empty(pageable);
            }
            
            // Filter companies by accessible IDs
            List<CompanyDTO> companies = companyRepository.findAll().stream()
                    .filter(company -> accessibleCompanyIds.contains(company.getId()))
                    .map(companyMapper::toDto)
                    .collect(Collectors.toList());
            
            // Apply pagination manually
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), companies.size());
            List<CompanyDTO> pageContent = companies.subList(start, end);
            
            return new PageImpl<>(pageContent, pageable, companies.size());
        }
        
        // Customer and others have no company access
        throw new UnauthorizedException("You don't have permission to view companies");
    }
    
    @Transactional(readOnly = true)
    public CompanyDTO getCompanyById(Long id) {
        // Check if user has access to this company
        if (!securityService.hasAccessToCompany(id)) {
            throw new UnauthorizedException("You don't have permission to view this company");
        }
        
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        return companyMapper.toDto(company);
    }
    
    @Transactional
    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        Company company = companyMapper.toEntity(companyDTO);
        
        if (company.getExternalId() == null) {
            company.setExternalId(UUID.randomUUID().toString());
        }
        
        if (companyDTO.getCompanyTypeId() != null) {
            CompanyType companyType = companyTypeRepository.findById(companyDTO.getCompanyTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("CompanyType", "id", companyDTO.getCompanyTypeId()));
            company.setCompanyType(companyType);
        }
        
        company = companyRepository.save(company);
        return companyMapper.toDto(company);
    }
    
    @Transactional
    public CompanyDTO updateCompany(Long id, CompanyDTO companyDTO) {
        // Check if user has permission to update this company
        // Only SUPERADMIN/ADMIN can update companies (B2B is read-only)
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to update companies");
        }
        
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        
        companyMapper.updateEntityFromDto(companyDTO, company);
        
        if (companyDTO.getCompanyTypeId() != null) {
            CompanyType companyType = companyTypeRepository.findById(companyDTO.getCompanyTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("CompanyType", "id", companyDTO.getCompanyTypeId()));
            company.setCompanyType(companyType);
        }
        
        company = companyRepository.save(company);
        return companyMapper.toDto(company);
    }
    
    @Transactional
    public void deleteCompany(Long id) {
        // Check if user has permission to delete this company
        // Only SUPERADMIN/ADMIN can delete companies
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to delete companies");
        }
        
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        companyRepository.delete(company);
    }
}

