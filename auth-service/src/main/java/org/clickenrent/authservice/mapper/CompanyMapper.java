package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.CompanyDTO;
import org.clickenrent.authservice.entity.Company;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Company entity and CompanyDTO.
 */
@Component
public class CompanyMapper {
    
    public CompanyDTO toDto(Company company) {
        if (company == null) {
            return null;
        }
        
        return CompanyDTO.builder()
                .id(company.getId())
                .externalId(company.getExternalId())
                .name(company.getName())
                .description(company.getDescription())
                .website(company.getWebsite())
                .logo(company.getLogo())
                .erpPartnerId(company.getErpPartnerId())
                .companyTypeId(company.getCompanyType() != null ? company.getCompanyType().getId() : null)
                .dateCreated(company.getDateCreated())
                .lastDateModified(company.getLastDateModified())
                .createdBy(company.getCreatedBy())
                .lastModifiedBy(company.getLastModifiedBy())
                .build();
    }
    
    public Company toEntity(CompanyDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Company.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .description(dto.getDescription())
                .website(dto.getWebsite())
                .logo(dto.getLogo())
                .erpPartnerId(dto.getErpPartnerId())
                .build();
    }
    
    public void updateEntityFromDto(CompanyDTO dto, Company company) {
        if (dto == null || company == null) {
            return;
        }
        
        if (dto.getName() != null) {
            company.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            company.setDescription(dto.getDescription());
        }
        if (dto.getWebsite() != null) {
            company.setWebsite(dto.getWebsite());
        }
        if (dto.getLogo() != null) {
            company.setLogo(dto.getLogo());
        }
        if (dto.getErpPartnerId() != null) {
            company.setErpPartnerId(dto.getErpPartnerId());
        }
    }
}


