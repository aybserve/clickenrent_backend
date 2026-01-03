package org.clickenrent.paymentservice.mapper;

import org.clickenrent.paymentservice.dto.UserPaymentProfileDTO;
import org.clickenrent.paymentservice.entity.UserPaymentProfile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for UserPaymentProfile entity and DTO
 */
@Component
public class UserPaymentProfileMapper {

    public UserPaymentProfileDTO toDTO(UserPaymentProfile entity) {
        if (entity == null) {
            return null;
        }
        
        return UserPaymentProfileDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .userExternalId(entity.getUserExternalId())
                .stripeCustomerId(entity.getStripeCustomerId())
                .isActive(entity.getIsActive())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public UserPaymentProfile toEntity(UserPaymentProfileDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return UserPaymentProfile.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .userExternalId(dto.getUserExternalId())
                .stripeCustomerId(dto.getStripeCustomerId())
                .isActive(dto.getIsActive())
                .build();
    }

    public List<UserPaymentProfileDTO> toDTOList(List<UserPaymentProfile> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}




