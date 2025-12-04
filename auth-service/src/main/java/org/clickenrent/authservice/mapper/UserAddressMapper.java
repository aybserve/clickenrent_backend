package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.UserAddressDTO;
import org.clickenrent.authservice.entity.UserAddress;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between UserAddress entity and UserAddressDTO.
 */
@Component
public class UserAddressMapper {
    
    public UserAddressDTO toDto(UserAddress userAddress) {
        if (userAddress == null) {
            return null;
        }
        
        return UserAddressDTO.builder()
                .id(userAddress.getId())
                .userId(userAddress.getUser() != null ? userAddress.getUser().getId() : null)
                .addressId(userAddress.getAddress() != null ? userAddress.getAddress().getId() : null)
                .build();
    }
    
    public UserAddress toEntity(UserAddressDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return UserAddress.builder()
                .id(dto.getId())
                .build();
    }
    
    public void updateEntityFromDto(UserAddressDTO dto, UserAddress userAddress) {
        if (dto == null || userAddress == null) {
            return;
        }
        
        // UserAddress is a join table, typically the relationships don't get updated
        // after creation, so this method is mostly a placeholder for consistency
    }
}

