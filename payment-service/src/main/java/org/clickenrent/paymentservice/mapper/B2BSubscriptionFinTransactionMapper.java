package org.clickenrent.paymentservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.B2BSubscriptionFinTransactionDTO;
import org.clickenrent.paymentservice.entity.B2BSubscriptionFinTransaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for B2BSubscriptionFinTransaction entity and DTO
 */
@Component
@RequiredArgsConstructor
public class B2BSubscriptionFinTransactionMapper {

    private final FinancialTransactionMapper financialTransactionMapper;

    public B2BSubscriptionFinTransactionDTO toDTO(B2BSubscriptionFinTransaction entity) {
        if (entity == null) {
            return null;
        }
        
        return B2BSubscriptionFinTransactionDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .b2bSubscriptionExternalId(entity.getB2bSubscriptionExternalId())
                .financialTransaction(financialTransactionMapper.toDTO(entity.getFinancialTransaction()))
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public B2BSubscriptionFinTransaction toEntity(B2BSubscriptionFinTransactionDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return B2BSubscriptionFinTransaction.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .b2bSubscriptionExternalId(dto.getB2bSubscriptionExternalId())
                .financialTransaction(financialTransactionMapper.toEntity(dto.getFinancialTransaction()))
                .build();
    }

    public List<B2BSubscriptionFinTransactionDTO> toDTOList(List<B2BSubscriptionFinTransaction> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}




