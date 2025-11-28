package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.CompanyDTO;
import org.clickenrent.authservice.entity.Company;
import org.clickenrent.authservice.entity.CompanyType;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.CompanyMapper;
import org.clickenrent.authservice.repository.CompanyRepository;
import org.clickenrent.authservice.repository.CompanyTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for managing Company entities.
 */
@Service
@RequiredArgsConstructor
public class CompanyService {
    
    private final CompanyRepository companyRepository;
    private final CompanyTypeRepository companyTypeRepository;
    private final CompanyMapper companyMapper;
    
    @Transactional(readOnly = true)
    public Page<CompanyDTO> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable)
                .map(companyMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public CompanyDTO getCompanyById(Long id) {
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
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        companyRepository.delete(company);
    }
}

