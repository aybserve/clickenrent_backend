package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.UserCompanyDTO;
import org.clickenrent.authservice.entity.UserCompany;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between UserCompany entity and UserCompanyDTO.
 */
@Component
public class UserCompanyMapper {
    
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
    
    public UserCompany toEntity(UserCompanyDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return UserCompany.builder()
                .id(dto.getId())
                .build();
    }
}

