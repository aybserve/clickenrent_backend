package org.clickenrent.authservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.UserCompanyDTO;
import org.clickenrent.authservice.dto.UserCompanyDetailDTO;
import org.clickenrent.authservice.entity.UserCompany;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between UserCompany entity and DTOs.
 */
@Component
@RequiredArgsConstructor
public class UserCompanyMapper {
    
    private final UserMapper userMapper;
    private final CompanyMapper companyMapper;
    private final CompanyRoleMapper companyRoleMapper;
    
    /**
     * Convert to simple DTO with only IDs (for POST/PUT operations).
     */
    public UserCompanyDTO toDto(UserCompany userCompany) {
        if (userCompany == null) {
            return null;
        }
        
        return UserCompanyDTO.builder()
                .id(userCompany.getId())
                .userId(userCompany.getUser() != null ? userCompany.getUser().getId() : null)
                .companyId(userCompany.getCompany() != null ? userCompany.getCompany().getId() : null)
                .companyRoleId(userCompany.getCompanyRole() != null ? userCompany.getCompanyRole().getId() : null)
                .build();
    }
    
    /**
     * Convert to detailed DTO with nested objects (for GET operations).
     */
    public UserCompanyDetailDTO toDetailDto(UserCompany userCompany) {
        if (userCompany == null) {
            return null;
        }
        
        return UserCompanyDetailDTO.builder()
                .id(userCompany.getId())
                .user(userMapper.toDto(userCompany.getUser()))
                .company(companyMapper.toDto(userCompany.getCompany()))
                .companyRole(companyRoleMapper.toDto(userCompany.getCompanyRole()))
                .build();
    }
    
    public UserCompany toEntity(UserCompanyDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return UserCompany.builder()
                .id(dto.getId())
                .build();
    }
}

