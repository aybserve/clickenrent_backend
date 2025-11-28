package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.UserDTO;
import org.clickenrent.authservice.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between User entity and UserDTO.
 * Password is never included in the DTO for security reasons.
 */
@Component
public class UserMapper {
    
    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .id(user.getId())
                .externalId(user.getExternalId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .city(user.getCity())
                .address(user.getAddress())
                .zipcode(user.getZipcode())
                .imageUrl(user.getImageUrl())
                .languageId(user.getLanguage() != null ? user.getLanguage().getId() : null)
                .isActive(user.getIsActive())
                .isDeleted(user.getIsDeleted())
                .dateCreated(user.getDateCreated())
                .lastDateModified(user.getLastDateModified())
                .createdBy(user.getCreatedBy())
                .lastModifiedBy(user.getLastModifiedBy())
                .build();
    }
    
    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return User.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .city(dto.getCity())
                .address(dto.getAddress())
                .zipcode(dto.getZipcode())
                .imageUrl(dto.getImageUrl())
                .isActive(dto.getIsActive())
                .isDeleted(dto.getIsDeleted())
                .build();
    }
    
    public void updateEntityFromDto(UserDTO dto, User user) {
        if (dto == null || user == null) {
            return;
        }
        
        if (dto.getUserName() != null) {
            user.setUserName(dto.getUserName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getCity() != null) {
            user.setCity(dto.getCity());
        }
        if (dto.getAddress() != null) {
            user.setAddress(dto.getAddress());
        }
        if (dto.getZipcode() != null) {
            user.setZipcode(dto.getZipcode());
        }
        if (dto.getImageUrl() != null) {
            user.setImageUrl(dto.getImageUrl());
        }
    }
}

