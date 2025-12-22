package org.clickenrent.paymentservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.B2BSaleFinTransactionDTO;
import org.clickenrent.paymentservice.entity.B2BSaleFinTransaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for B2BSaleFinTransaction entity and DTO
 */
@Component
@RequiredArgsConstructor
public class B2BSaleFinTransactionMapper {

    private final FinancialTransactionMapper financialTransactionMapper;

    public B2BSaleFinTransactionDTO toDTO(B2BSaleFinTransaction entity) {
        if (entity == null) {
            return null;
        }
        
        return B2BSaleFinTransactionDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .b2bSaleId(entity.getB2bSaleId())
                .financialTransaction(financialTransactionMapper.toDTO(entity.getFinancialTransaction()))
                .build();
    }

    public B2BSaleFinTransaction toEntity(B2BSaleFinTransactionDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return B2BSaleFinTransaction.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .b2bSaleId(dto.getB2bSaleId())
                .financialTransaction(financialTransactionMapper.toEntity(dto.getFinancialTransaction()))
                .build();
    }

    public List<B2BSaleFinTransactionDTO> toDTOList(List<B2BSaleFinTransaction> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}




