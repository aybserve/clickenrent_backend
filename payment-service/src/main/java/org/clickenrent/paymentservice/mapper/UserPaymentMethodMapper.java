package org.clickenrent.paymentservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.UserPaymentMethodDTO;
import org.clickenrent.paymentservice.entity.UserPaymentMethod;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for UserPaymentMethod entity and DTO
 */
@Component
@RequiredArgsConstructor
public class UserPaymentMethodMapper {

    private final UserPaymentProfileMapper userPaymentProfileMapper;
    private final PaymentMethodMapper paymentMethodMapper;

    public UserPaymentMethodDTO toDTO(UserPaymentMethod entity) {
        if (entity == null) {
            return null;
        }
        
        return UserPaymentMethodDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .userPaymentProfile(userPaymentProfileMapper.toDTO(entity.getUserPaymentProfile()))
                .paymentMethod(paymentMethodMapper.toDTO(entity.getPaymentMethod()))
                .stripePaymentMethodId(entity.getStripePaymentMethodId())
                .isDefault(entity.getIsDefault())
                .isActive(entity.getIsActive())
                .build();
    }

    public UserPaymentMethod toEntity(UserPaymentMethodDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return UserPaymentMethod.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .userPaymentProfile(userPaymentProfileMapper.toEntity(dto.getUserPaymentProfile()))
                .paymentMethod(paymentMethodMapper.toEntity(dto.getPaymentMethod()))
                .stripePaymentMethodId(dto.getStripePaymentMethodId())
                .isDefault(dto.getIsDefault())
                .isActive(dto.getIsActive())
                .build();
    }

    public List<UserPaymentMethodDTO> toDTOList(List<UserPaymentMethod> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}







