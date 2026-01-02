package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.CompanyTypeDTO;
import org.clickenrent.authservice.entity.CompanyType;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.CompanyTypeMapper;
import org.clickenrent.authservice.repository.CompanyTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing CompanyType entities.
 */
@Service
@RequiredArgsConstructor
public class CompanyTypeService {
    
    private final CompanyTypeRepository companyTypeRepository;
    private final CompanyTypeMapper companyTypeMapper;
    
    @Transactional(readOnly = true)
    public List<CompanyTypeDTO> getAllCompanyTypes() {
        return companyTypeRepository.findAll().stream()
                .map(companyTypeMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CompanyTypeDTO getCompanyTypeById(Long id) {
        CompanyType companyType = companyTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyType", "id", id));
        return companyTypeMapper.toDto(companyType);
    }
    
    @Transactional(readOnly = true)
    public CompanyTypeDTO getCompanyTypeByExternalId(String externalId) {
        CompanyType companyType = companyTypeRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyType", "externalId", externalId));
        return companyTypeMapper.toDto(companyType);
    }
    
    @Transactional
    public CompanyTypeDTO createCompanyType(CompanyTypeDTO companyTypeDTO) {
        CompanyType companyType = companyTypeMapper.toEntity(companyTypeDTO);
        companyType = companyTypeRepository.save(companyType);
        return companyTypeMapper.toDto(companyType);
    }
    
    @Transactional
    public CompanyTypeDTO updateCompanyType(Long id, CompanyTypeDTO companyTypeDTO) {
        CompanyType companyType = companyTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyType", "id", id));
        
        companyTypeMapper.updateEntityFromDto(companyTypeDTO, companyType);
        companyType = companyTypeRepository.save(companyType);
        return companyTypeMapper.toDto(companyType);
    }
    
    @Transactional
    public void deleteCompanyType(Long id) {
        CompanyType companyType = companyTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyType", "id", id));
        companyTypeRepository.delete(companyType);
    }
}










